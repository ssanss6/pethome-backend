package com.pethome.service;

import com.pethome.dto.BoardingOrderCreateDTO;
import com.pethome.dto.CheckInDTO;
import com.pethome.dto.CheckoutDTO;
import com.pethome.entity.BoardingOrder;
import com.pethome.entity.Cage;
import com.pethome.entity.Settlement;
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

    /** 入住登记（生成照护任务，待入住 -> 寄养中） */
    void checkIn(Long id, CheckInDTO dto);

    /** 离店结算（发起结算单，寄养中 -> 待离店） */
    Settlement checkout(Long id, CheckoutDTO dto);

    /** 离店确认（结算完成，待离店 -> 已完成） */
    void complete(Long id);
}
