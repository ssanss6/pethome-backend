package com.pethome.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pethome.entity.BoardingOrder;
import com.pethome.enums.BoardingOrderStatus;
import com.pethome.mapper.BoardingOrderMapper;
import com.pethome.service.CareTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 照护任务补生成定时任务（幂等，已存在的任务会跳过）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CareTaskCompensateJob {

    private final BoardingOrderMapper boardingOrderMapper;
    private final CareTaskService careTaskService;

    @Scheduled(fixedDelay = 5 * 60_000)
    public void compensateTasks() {
        List<BoardingOrder> boarding = boardingOrderMapper.selectList(new LambdaQueryWrapper<BoardingOrder>()
                .eq(BoardingOrder::getStatus, BoardingOrderStatus.BOARDING.getCode()));
        for (BoardingOrder o : boarding) {
            careTaskService.generateTasks(o.getId(), o.getCheckInDate(), o.getCheckOutDate());
        }
    }
}
