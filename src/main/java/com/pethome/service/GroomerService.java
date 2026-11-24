package com.pethome.service;

import com.pethome.dto.GroomerDTO;
import com.pethome.entity.Groomer;

import java.util.List;

public interface GroomerService {

    List<Groomer> list();

    Groomer create(GroomerDTO dto);

    Groomer update(Long id, GroomerDTO dto);

    void delete(Long id);
}
