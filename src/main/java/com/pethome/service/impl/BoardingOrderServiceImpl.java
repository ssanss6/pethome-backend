package com.pethome.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.pethome.common.exception.BizException;
import com.pethome.common.result.ErrorCode;
import com.pethome.dto.BoardingOrderCreateDTO;
import com.pethome.dto.CheckInDTO;
import com.pethome.dto.CheckoutDTO;
import com.pethome.entity.BoardingOrder;
import com.pethome.entity.Cage;
import com.pethome.entity.CageOccupancy;
import com.pethome.entity.OrderStatusLog;
import com.pethome.entity.Pet;
import com.pethome.entity.RoomType;
import com.pethome.entity.Settlement;
import com.pethome.enums.BoardingOrderStatus;
import com.pethome.mapper.BoardingOrderMapper;
import com.pethome.mapper.CageMapper;
import com.pethome.mapper.CageOccupancyMapper;
import com.pethome.mapper.OrderStatusLogMapper;
import com.pethome.mapper.PetMapper;
import com.pethome.mapper.RoomTypeMapper;
import com.pethome.mapper.SettlementMapper;
import com.pethome.security.UserContext;
import com.pethome.service.BoardingOrderService;
import com.pethome.service.CareTaskService;
import com.pethome.service.RedisService;
import com.pethome.vo.BoardingOrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 寄养预约服务实现（区间库存 + 计价 + 订单状态机）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BoardingOrderServiceImpl implements BoardingOrderService {

    private static final long DEFAULT_STORE_ID = 1L;

    private final BoardingOrderMapper boardingOrderMapper;
    private final CageOccupancyMapper cageOccupancyMapper;
    private final OrderStatusLogMapper orderStatusLogMapper;
    private final CageMapper cageMapper;
    private final RoomTypeMapper roomTypeMapper;
    private final PetMapper petMapper;
    private final RedisService redisService;
    private final CareTaskService careTaskService;
    private final SettlementMapper settlementMapper;

    /**
     * 查询某房型在 [checkIn, checkOut) 区间内的可订笼位
     *
     * @param roomTypeId  房型ID
     * @param checkInDate 入住日（含）
     * @param checkOutDate 离店日（不含，半开区间）
     * @return 可订笼位列表
     */
    @Override
    public List<Cage> listAvailableCages(Long roomTypeId, LocalDate checkInDate, LocalDate checkOutDate) {
        validateDateRange(checkInDate, checkOutDate);
        String cacheKey = availCacheKey(roomTypeId, checkInDate, checkOutDate);
        //0、先查缓存（短 TTL，最终一致性以数据库为准；缓存不可用自动回源）
        try {
            List<Cage> cached = redisService.get(cacheKey, new TypeReference<List<Cage>>() {});
            if (cached != null) {
                return cached;
            }
        } catch (Exception e) {
            log.warn("可订笼位缓存读取失败，回源数据库：{}", e.getMessage());
        }
        //1、该房型所有启用笼位
        List<Cage> cages = cageMapper.selectList(new LambdaQueryWrapper<Cage>()
                .eq(Cage::getRoomTypeId, roomTypeId)
                .eq(Cage::getStatus, 1)
                .orderByAsc(Cage::getCageNo));
        if (cages.isEmpty()) {
            return List.of();
        }
        //2、区间内已占用的笼位
        List<Long> cageIds = cages.stream().map(Cage::getId).toList();
        List<CageOccupancy> occupancies = cageOccupancyMapper.selectList(new LambdaQueryWrapper<CageOccupancy>()
                .in(CageOccupancy::getCageId, cageIds)
                .ge(CageOccupancy::getOccupyDate, checkInDate)
                .lt(CageOccupancy::getOccupyDate, checkOutDate));
        Set<Long> occupiedCageIds = occupancies.stream()
                .map(CageOccupancy::getCageId)
                .collect(Collectors.toSet());
        //3、过滤掉已占用的笼位
        List<Cage> result = cages.stream().filter(c -> !occupiedCageIds.contains(c.getId())).toList();
        //4、写缓存（60 秒）
        try {
            redisService.set(cacheKey, result, 60, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("可订笼位缓存写入失败：{}", e.getMessage());
        }
        return result;
    }

    /**
     * 创建寄养订单（校验宠物/房型/日期区间/库存 + 计价）
     *
     * @param dto 下单入参
     * @return 订单实体（状态：待支付）
     */
    @Override
    @Transactional
    public BoardingOrder create(BoardingOrderCreateDTO dto) {
        //1、校验宠物归属
        Pet pet = petMapper.selectById(dto.getPetId());
        if (pet == null || !pet.getOwnerId().equals(UserContext.getUserId())) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND, "宠物不存在");
        }
        //2、校验房型
        RoomType roomType = roomTypeMapper.selectById(dto.getRoomTypeId());
        if (roomType == null || roomType.getStatus() != 1) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND, "房型不存在");
        }
        //3、校验日期区间并计算晚数（半开区间 [入住, 离店)）
        validateDateRange(dto.getCheckInDate(), dto.getCheckOutDate());
        int nights = (int) ChronoUnit.DAYS.between(dto.getCheckInDate(), dto.getCheckOutDate());
        //4、区间库存校验：有可订笼位才能下单
        List<Cage> available = listAvailableCages(dto.getRoomTypeId(), dto.getCheckInDate(), dto.getCheckOutDate());
        if (available.isEmpty()) {
            throw new BizException(ErrorCode.CAGE_CONFLICT, "该日期区间已满房");
        }
        //5、计价并生成订单
        BoardingOrder order = new BoardingOrder();
        order.setOrderNo(generateOrderNo());
        order.setOwnerId(UserContext.getUserId());
        order.setPetId(dto.getPetId());
        order.setStoreId(DEFAULT_STORE_ID);
        order.setRoomTypeId(dto.getRoomTypeId());
        order.setCheckInDate(dto.getCheckInDate());
        order.setCheckOutDate(dto.getCheckOutDate());
        order.setNights(nights);
        order.setStatus(BoardingOrderStatus.PENDING_PAY.getCode());
        order.setBaseAmount(roomType.getDailyPrice().multiply(BigDecimal.valueOf(nights)));
        order.setDepositAmount(BigDecimal.ZERO);
        order.setExtraAmount(BigDecimal.ZERO);
        order.setTimeoutFee(BigDecimal.ZERO);
        order.setRemark(dto.getRemark());
        boardingOrderMapper.insert(order);
        //6、写初始状态日志
        writeStatusLog(order.getId(), null, BoardingOrderStatus.PENDING_PAY, "下单");
        return order;
    }

    /**
     * 我的寄养订单列表
     *
     * @return 订单列表
     */
    @Override
    public List<BoardingOrder> listMine() {
        return boardingOrderMapper.selectList(new LambdaQueryWrapper<BoardingOrder>()
                .eq(BoardingOrder::getOwnerId, UserContext.getUserId())
                .orderByDesc(BoardingOrder::getId));
    }

    /**
     * 订单详情（含宠物/房型/笼位名称 + 状态时间线）
     *
     * @param id 订单ID
     * @return 详情 VO
     */
    @Override
    public BoardingOrderVO getMine(Long id) {
        BoardingOrder order = getMineOrder(id);
        BoardingOrderVO vo = new BoardingOrderVO();
        BeanUtil.copyProperties(order, vo);
        vo.setStatusDesc(BoardingOrderStatus.fromCode(order.getStatus()).getDesc());
        // 宠物名
        Pet pet = petMapper.selectById(order.getPetId());
        if (pet != null) {
            vo.setPetName(pet.getName());
        }
        // 房型名
        RoomType roomType = roomTypeMapper.selectById(order.getRoomTypeId());
        if (roomType != null) {
            vo.setRoomTypeName(roomType.getName());
        }
        // 笼位号
        if (order.getCageId() != null) {
            Cage cage = cageMapper.selectById(order.getCageId());
            if (cage != null) {
                vo.setCageNo(cage.getCageNo());
            }
        }
        // 状态时间线
        List<OrderStatusLog> logs = orderStatusLogMapper.selectList(new LambdaQueryWrapper<OrderStatusLog>()
                .eq(OrderStatusLog::getOrderType, "BOARDING")
                .eq(OrderStatusLog::getOrderId, id)
                .orderByAsc(OrderStatusLog::getId));
        vo.setStatusLogs(logs);
        return vo;
    }

    /**
     * 模拟支付（待支付 -> 待确认）
     *
     * @param id 订单ID
     */
    @Override
    @Transactional
    public void pay(Long id) {
        BoardingOrder order = getMineOrder(id);
        transition(order, BoardingOrderStatus.PENDING_CONFIRM, "模拟支付成功");
    }

    /**
     * 用户取消订单
     *
     * @param id 订单ID
     */
    @Override
    @Transactional
    public void cancel(Long id) {
        BoardingOrder order = getMineOrder(id);
        transition(order, BoardingOrderStatus.CANCELLED, "用户取消");
    }

    /**
     * 门店确认（分配并锁定笼位，待确认 -> 待入住）
     *
     * @param id 订单ID
     */
    @Override
    @Transactional
    public void confirm(Long id) {
        BoardingOrder order = boardingOrderMapper.selectById(id);
        if (order == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND, "订单不存在");
        }
        // 只有待确认状态才能门店确认
        if (!BoardingOrderStatus.PENDING_CONFIRM.getCode().equals(order.getStatus())) {
            throw new BizException(ErrorCode.ORDER_STATUS_ERROR, "订单状态不允许确认");
        }
        // 分配一个可订笼位
        List<Cage> available = listAvailableCages(order.getRoomTypeId(), order.getCheckInDate(), order.getCheckOutDate());
        if (available.isEmpty()) {
            throw new BizException(ErrorCode.CAGE_CONFLICT, "该日期区间已满房");
        }
        Cage cage = available.get(0);
        // 按天拆占用记录，(cage_id, occupy_date) 唯一索引兜底防重叠
        LocalDate d = order.getCheckInDate();
        while (d.isBefore(order.getCheckOutDate())) {
            CageOccupancy occ = new CageOccupancy();
            occ.setCageId(cage.getId());
            occ.setOccupyDate(d);
            occ.setBoardingOrderId(order.getId());
            cageOccupancyMapper.insert(occ);
            d = d.plusDays(1);
        }
        // 绑定笼位并流转状态
        order.setCageId(cage.getId());
        transition(order, BoardingOrderStatus.PENDING_CHECK_IN, "门店确认，锁定笼位 " + cage.getCageNo());
        // 失效该日期区间的可订笼位缓存（Redis 不可用时忽略）
        try {
            redisService.delete(availCacheKey(order.getRoomTypeId(), order.getCheckInDate(), order.getCheckOutDate()));
        } catch (Exception e) {
            log.warn("可订笼位缓存失效失败：{}", e.getMessage());
        }
    }

    /**
     * 入住登记（记录押金、生成照护任务，待入住 -> 寄养中）
     *
     * @param id  订单ID
     * @param dto 入住信息
     */
    @Override
    @Transactional
    public void checkIn(Long id, CheckInDTO dto) {
        BoardingOrder order = boardingOrderMapper.selectById(id);
        if (order == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND, "订单不存在");
        }
        // 只有待入住状态才能办理入住
        if (!BoardingOrderStatus.PENDING_CHECK_IN.getCode().equals(order.getStatus())) {
            throw new BizException(ErrorCode.ORDER_STATUS_ERROR, "订单状态不允许入住登记");
        }
        // 记录押金
        if (dto.getDepositAmount() != null) {
            order.setDepositAmount(dto.getDepositAmount());
        }
        // 生成每日照护任务
        careTaskService.generateTasks(order.getId(), order.getCheckInDate(), order.getCheckOutDate());
        // 状态流转：待入住 -> 寄养中
        transition(order, BoardingOrderStatus.BOARDING, "办理入住");
    }

    /**
     * 离店结算（发起结算单，寄养中 -> 待离店）
     */
    @Override
    @Transactional
    public Settlement checkout(Long id, CheckoutDTO dto) {
        BoardingOrder order = boardingOrderMapper.selectById(id);
        if (order == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND, "订单不存在");
        }
        if (!BoardingOrderStatus.BOARDING.getCode().equals(order.getStatus())) {
            throw new BizException(ErrorCode.ORDER_STATUS_ERROR, "订单状态不允许结算");
        }
        BigDecimal base = order.getBaseAmount() == null ? BigDecimal.ZERO : order.getBaseAmount();
        BigDecimal extra = dto.getExtraAmount() == null ? BigDecimal.ZERO : dto.getExtraAmount();
        BigDecimal timeout = dto.getTimeoutFee() == null ? BigDecimal.ZERO : dto.getTimeoutFee();
        BigDecimal deposit = order.getDepositAmount() == null ? BigDecimal.ZERO : order.getDepositAmount();
        BigDecimal total = base.add(extra).add(timeout).subtract(deposit);

        Settlement settlement = new Settlement();
        settlement.setSettlementNo("S" + System.currentTimeMillis());
        settlement.setBoardingOrderId(order.getId());
        settlement.setBaseAmount(base);
        settlement.setExtraAmount(extra);
        settlement.setTimeoutFee(timeout);
        settlement.setDepositAmount(deposit);
        settlement.setTotalAmount(total);
        settlement.setStatus("PENDING");
        settlementMapper.insert(settlement);

        transition(order, BoardingOrderStatus.PENDING_CHECK_OUT, "发起离店结算");
        return settlement;
    }

    /**
     * 离店确认（结算完成，待离店 -> 已完成）
     */
    @Override
    @Transactional
    public void complete(Long id) {
        BoardingOrder order = boardingOrderMapper.selectById(id);
        if (order == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND, "订单不存在");
        }
        if (!BoardingOrderStatus.PENDING_CHECK_OUT.getCode().equals(order.getStatus())) {
            throw new BizException(ErrorCode.ORDER_STATUS_ERROR, "订单状态不允许离店确认");
        }
        transition(order, BoardingOrderStatus.COMPLETED, "离店完成");
        Settlement settlement = settlementMapper.selectOne(new LambdaQueryWrapper<Settlement>()
                .eq(Settlement::getBoardingOrderId, order.getId()));
        if (settlement != null) {
            settlement.setStatus("SETTLED");
            settlementMapper.updateById(settlement);
        }
    }

    // ---------- 私有方法 ----------

    private BoardingOrder getMineOrder(Long id) {
        BoardingOrder order = boardingOrderMapper.selectById(id);
        if (order == null || !order.getOwnerId().equals(UserContext.getUserId())) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND, "订单不存在");
        }
        return order;
    }

    private void validateDateRange(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null || !checkIn.isBefore(checkOut)) {
            throw new BizException(ErrorCode.PARAM_ERROR, "入住日期必须早于离店日期");
        }
    }

    /** 集中式状态流转：校验合法性 + 更新订单 + 写日志 */
    private void transition(BoardingOrder order, BoardingOrderStatus target, String reason) {
        BoardingOrderStatus from = BoardingOrderStatus.fromCode(order.getStatus());
        if (!from.canTransitionTo(target)) {
            throw new BizException(ErrorCode.ORDER_STATUS_ERROR, "订单状态不允许该操作");
        }
        order.setStatus(target.getCode());
        boardingOrderMapper.updateById(order);
        writeStatusLog(order.getId(), from, target, reason);
    }

    private void writeStatusLog(Long orderId, BoardingOrderStatus from, BoardingOrderStatus to, String reason) {
        OrderStatusLog log = new OrderStatusLog();
        log.setOrderType("BOARDING");
        log.setOrderId(orderId);
        log.setFromStatus(from == null ? null : from.getCode());
        log.setToStatus(to.getCode());
        log.setOperatorId(UserContext.getUserId());
        log.setReason(reason);
        orderStatusLogMapper.insert(log);
    }

    private String availCacheKey(Long roomTypeId, LocalDate checkIn, LocalDate checkOut) {
        return "cache:boarding:avail:" + roomTypeId + ":" + checkIn + ":" + checkOut;
    }

    private String generateOrderNo() {
        return "BO" + System.currentTimeMillis() + String.format("%03d", new Random().nextInt(1000));
    }
}
