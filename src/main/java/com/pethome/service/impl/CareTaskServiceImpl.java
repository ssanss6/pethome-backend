package com.pethome.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pethome.common.exception.BizException;
import com.pethome.common.result.ErrorCode;
import com.pethome.dto.CareTaskCompleteDTO;
import com.pethome.entity.BoardingOrder;
import com.pethome.entity.Cage;
import com.pethome.entity.CareTask;
import com.pethome.entity.Pet;
import com.pethome.entity.RoomType;
import com.pethome.enums.CareTaskStatus;
import com.pethome.enums.CareTaskType;
import com.pethome.mapper.BoardingOrderMapper;
import com.pethome.mapper.CageMapper;
import com.pethome.mapper.CareTaskMapper;
import com.pethome.mapper.PetMapper;
import com.pethome.mapper.RoomTypeMapper;
import com.pethome.security.UserContext;
import com.pethome.service.CareTaskService;
import com.pethome.vo.CareTaskVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 照护任务服务实现
 */
@Service
@RequiredArgsConstructor
public class CareTaskServiceImpl implements CareTaskService {

    private final CareTaskMapper careTaskMapper;
    private final BoardingOrderMapper boardingOrderMapper;
    private final PetMapper petMapper;
    private final CageMapper cageMapper;
    private final RoomTypeMapper roomTypeMapper;

    @Override
    @Transactional
    public void generateTasks(Long boardingOrderId, LocalDate checkInDate, LocalDate checkOutDate) {
        // 半开区间 [入住, 离店)，每天生成 4 类任务，(order_id, date, type) 唯一键防重复
        LocalDate d = checkInDate;
        while (d.isBefore(checkOutDate)) {
            for (CareTaskType type : CareTaskType.values()) {
                CareTask task = new CareTask();
                task.setBoardingOrderId(boardingOrderId);
                task.setTaskDate(d);
                task.setTaskType(type.getCode());
                task.setStatus(CareTaskStatus.PENDING.getCode());
                careTaskMapper.insert(task);
            }
            d = d.plusDays(1);
        }
    }

    @Override
    public List<CareTaskVO> listByDate(LocalDate date) {
        List<CareTask> tasks = careTaskMapper.selectList(new LambdaQueryWrapper<CareTask>()
                .eq(CareTask::getTaskDate, date)
                .orderByAsc(CareTask::getId));
        return tasks.stream().map(this::toVO).toList();
    }

    @Override
    public List<CareTaskVO> listByOrder(Long orderId, Long ownerId) {
        // 校验订单归属
        BoardingOrder order = boardingOrderMapper.selectById(orderId);
        if (order == null || !order.getOwnerId().equals(ownerId)) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND, "订单不存在");
        }
        List<CareTask> tasks = careTaskMapper.selectList(new LambdaQueryWrapper<CareTask>()
                .eq(CareTask::getBoardingOrderId, orderId)
                .orderByAsc(CareTask::getTaskDate)
                .orderByAsc(CareTask::getId));
        return tasks.stream().map(this::toVO).toList();
    }

    @Override
    public void complete(Long taskId, CareTaskCompleteDTO dto) {
        CareTask task = careTaskMapper.selectById(taskId);
        if (task == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND, "任务不存在");
        }
        if (CareTaskStatus.DONE.getCode().equals(task.getStatus())) {
            throw new BizException(ErrorCode.DUPLICATE_SUBMIT, "任务已打卡，请勿重复操作");
        }
        task.setStatus(CareTaskStatus.DONE.getCode());
        task.setOperatorId(UserContext.getUserId());
        task.setDoneAt(LocalDateTime.now());
        task.setPhotoUrl(dto.getPhotoUrl());
        task.setNote(dto.getNote());
        careTaskMapper.updateById(task);
    }

    private CareTaskVO toVO(CareTask task) {
        CareTaskVO vo = new CareTaskVO();
        BeanUtil.copyProperties(task, vo);
        vo.setTaskTypeDesc(taskTypeDesc(task.getTaskType()));
        vo.setStatusDesc(statusDesc(task.getStatus()));
        BoardingOrder order = boardingOrderMapper.selectById(task.getBoardingOrderId());
        if (order != null) {
            vo.setOrderNo(order.getOrderNo());
            Pet pet = petMapper.selectById(order.getPetId());
            if (pet != null) {
                vo.setPetName(pet.getName());
            }
            if (order.getCageId() != null) {
                Cage cage = cageMapper.selectById(order.getCageId());
                if (cage != null) {
                    vo.setCageNo(cage.getCageNo());
                }
            }
            RoomType roomType = roomTypeMapper.selectById(order.getRoomTypeId());
            if (roomType != null) {
                vo.setRoomTypeName(roomType.getName());
            }
        }
        return vo;
    }

    private String taskTypeDesc(String code) {
        for (CareTaskType t : CareTaskType.values()) {
            if (t.getCode().equals(code)) {
                return t.getDesc();
            }
        }
        return code;
    }

    private String statusDesc(String code) {
        for (CareTaskStatus s : CareTaskStatus.values()) {
            if (s.getCode().equals(code)) {
                return s.getDesc();
            }
        }
        return code;
    }
}
