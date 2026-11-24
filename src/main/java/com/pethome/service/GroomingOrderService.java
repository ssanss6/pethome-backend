package com.pethome.service;

import com.pethome.dto.GroomingOrderCreateDTO;
import com.pethome.entity.GroomingOrder;

import java.util.List;

public interface GroomingOrderService {

    GroomingOrder create(GroomingOrderCreateDTO dto);

    void pay(Long id);

    void cancel(Long id);

    void signIn(Long id);

    void complete(Long id);

    List<GroomingOrder> listMine();

    /** 全部洗护订单（员工） */
    List<GroomingOrder> listAll();
}
