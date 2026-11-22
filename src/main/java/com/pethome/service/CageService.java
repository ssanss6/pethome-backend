package com.pethome.service;

import com.pethome.dto.CageDTO;
import com.pethome.entity.Cage;

import java.util.List;

public interface CageService {

    List<Cage> list(Long roomTypeId);

    Cage get(Long id);

    Cage create(CageDTO dto);

    Cage update(Long id, CageDTO dto);

    void delete(Long id);
}
