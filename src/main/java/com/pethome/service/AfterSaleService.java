package com.pethome.service;

import com.pethome.dto.AfterSaleCreateDTO;
import com.pethome.entity.AfterSale;

import java.util.List;

public interface AfterSaleService {

    AfterSale create(AfterSaleCreateDTO dto);

    List<AfterSale> listMine();

    List<AfterSale> listAll();

    void handle(Long id, String status);
}
