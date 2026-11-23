package com.pethome.service;

import com.pethome.dto.BoardingOrderCreateDTO;
import com.pethome.entity.BoardingOrder;
import com.pethome.entity.Cage;
import com.pethome.vo.BoardingOrderVO;

import java.time.LocalDate;
import java.util.List;

public interface BoardingOrderService {

    /** 查询某房型在日期区间内的可订笼位 */
    List<Cage> listAvailableCages(Long roomTypeId, LocalDate checkInDate, LocalDate checkOutDate);

    /** 创建寄养订单（区间校验 + 计价） */
    BoardingOrder create(BoardingOrderCreateDTO dto);

    /** 我的寄养订单列表 */
    List<BoardingOrder> listMine();

    /** 订单详情（含状态时间线） */
    BoardingOrderVO getMine(Long id);

    /** 模拟支付（待支付 -> 待确认） */
    void pay(Long id);

    /** 用户取消 */
    void cancel(Long id);

    /** 门店确认（分配并锁定笼位，待确认 -> 待入住） */
    void confirm(Long id);
}
