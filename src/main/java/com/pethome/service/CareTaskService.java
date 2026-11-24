package com.pethome.service;

import com.pethome.dto.CareTaskCompleteDTO;
import com.pethome.vo.CareTaskVO;

import java.time.LocalDate;
import java.util.List;

public interface CareTaskService {

    /** 按入住日期区间生成每日照护任务（喂食/遛弯/清洁/拍照，唯一键防重复） */
    void generateTasks(Long boardingOrderId, LocalDate checkInDate, LocalDate checkOutDate);

    /** 某日照护任务列表（员工） */
    List<CareTaskVO> listByDate(LocalDate date);

    /** 某订单照护任务列表（宠物主，校验归属） */
    List<CareTaskVO> listByOrder(Long orderId, Long ownerId);

    /** 打卡完成任务 */
    void complete(Long taskId, CareTaskCompleteDTO dto);
}
