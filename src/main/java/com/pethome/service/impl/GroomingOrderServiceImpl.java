package com.pethome.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pethome.common.exception.BizException;
import com.pethome.common.result.ErrorCode;
import com.pethome.dto.GroomingOrderCreateDTO;
import com.pethome.entity.Groomer;
import com.pethome.entity.GroomingOrder;
import com.pethome.entity.Pet;
import com.pethome.entity.ServiceItem;
import com.pethome.enums.GroomingOrderStatus;
import com.pethome.mapper.GroomerMapper;
import com.pethome.mapper.GroomingOrderMapper;
import com.pethome.mapper.PetMapper;
import com.pethome.mapper.ServiceItemMapper;
import com.pethome.security.UserContext;
import com.pethome.service.GroomingOrderService;
import com.pethome.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 洗护订单服务实现
 */
@Service
@RequiredArgsConstructor
public class GroomingOrderServiceImpl implements GroomingOrderService {

    private final GroomingOrderMapper groomingOrderMapper;
    private final ServiceItemMapper serviceItemMapper;
    private final GroomerMapper groomerMapper;
    private final PetMapper petMapper;
    private final PaymentService paymentService;

    @Override
    @Transactional
    public GroomingOrder create(GroomingOrderCreateDTO dto) {
        if (dto.getPetId() == null || dto.getServiceItemId() == null || dto.getGroomerId() == null
                || dto.getServiceDate() == null || !StringUtils.hasText(dto.getSlot())) {
            throw new BizException(ErrorCode.PARAM_ERROR, "预约信息不完整");
        }
        Pet pet = petMapper.selectById(dto.getPetId());
        if (pet == null || !pet.getOwnerId().equals(UserContext.getUserId())) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND, "宠物不存在");
        }
        ServiceItem item = serviceItemMapper.selectById(dto.getServiceItemId());
        if (item == null || item.getStatus() != 1) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND, "服务项目不存在");
        }
        Groomer groomer = groomerMapper.selectById(dto.getGroomerId());
        if (groomer == null || groomer.getStatus() != 1) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND, "美容师不存在");
        }
        // 时段冲突预判（唯一键兜底）
        Long conflict = groomingOrderMapper.selectCount(new LambdaQueryWrapper<GroomingOrder>()
                .eq(GroomingOrder::getGroomerId, dto.getGroomerId())
                .eq(GroomingOrder::getServiceDate, dto.getServiceDate())
                .eq(GroomingOrder::getSlot, dto.getSlot())
                .in(GroomingOrder::getStatus, GroomingOrderStatus.PENDING_PAY.getCode(),
                        GroomingOrderStatus.BOOKED.getCode(), GroomingOrderStatus.IN_SERVICE.getCode()));
        if (conflict != null && conflict > 0) {
            throw new BizException(ErrorCode.SLOT_FULL, "该美容师此时段已被预约");
        }
        GroomingOrder order = new GroomingOrder();
        order.setOrderNo(generateNo());
        order.setOwnerId(UserContext.getUserId());
        order.setPetId(dto.getPetId());
        order.setStoreId(1L);
        order.setServiceItemId(dto.getServiceItemId());
        order.setGroomerId(dto.getGroomerId());
        order.setServiceDate(dto.getServiceDate());
        order.setSlot(dto.getSlot());
        order.setStatus(GroomingOrderStatus.PENDING_PAY.getCode());
        order.setAmount(item.getPrice());
        order.setRemark(dto.getRemark());
        groomingOrderMapper.insert(order);
        return order;
    }

    @Override
    public void pay(Long id) {
        GroomingOrder order = getOrder(id);
        transition(order, GroomingOrderStatus.BOOKED);
        paymentService.pay("GROOMING", id, order.getAmount());
    }

    @Override
    public void cancel(Long id) {
        GroomingOrder order = getOrder(id);
        transition(order, GroomingOrderStatus.CANCELLED);
    }

    @Override
    public void signIn(Long id) {
        GroomingOrder order = getOrder(id);
        transition(order, GroomingOrderStatus.IN_SERVICE);
    }

    @Override
    public void complete(Long id) {
        GroomingOrder order = getOrder(id);
        transition(order, GroomingOrderStatus.COMPLETED);
    }

    @Override
    public List<GroomingOrder> listMine() {
        return groomingOrderMapper.selectList(new LambdaQueryWrapper<GroomingOrder>()
                .eq(GroomingOrder::getOwnerId, UserContext.getUserId())
                .orderByDesc(GroomingOrder::getId));
    }

    @Override
    public List<GroomingOrder> listAll() {
        return groomingOrderMapper.selectList(new LambdaQueryWrapper<GroomingOrder>()
                .orderByDesc(GroomingOrder::getId));
    }

    private GroomingOrder getOrder(Long id) {
        GroomingOrder order = groomingOrderMapper.selectById(id);
        if (order == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND, "订单不存在");
        }
        return order;
    }

    private void transition(GroomingOrder order, GroomingOrderStatus target) {
        GroomingOrderStatus current = GroomingOrderStatus.fromCode(order.getStatus());
        if (!current.canTransitionTo(target)) {
            throw new BizException(ErrorCode.ORDER_STATUS_ERROR, "订单状态不允许该操作");
        }
        order.setStatus(target.getCode());
        groomingOrderMapper.updateById(order);
    }

    private String generateNo() {
        return "G" + System.currentTimeMillis() + (int) (Math.random() * 9000 + 1000);
    }
}
