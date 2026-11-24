package com.pethome.service;

import com.pethome.dto.ServiceItemDTO;
import com.pethome.entity.ServiceItem;

import java.util.List;

public interface ServiceItemService {

    List<ServiceItem> list();

    ServiceItem create(ServiceItemDTO dto);

    ServiceItem update(Long id, ServiceItemDTO dto);

    void delete(Long id);
}
