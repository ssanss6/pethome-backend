package com.pethome.service;

import com.pethome.dto.RoomTypeDTO;
import com.pethome.entity.RoomType;

import java.util.List;

public interface RoomTypeService {

    List<RoomType> list();

    RoomType get(Long id);

    RoomType create(RoomTypeDTO dto);

    RoomType update(Long id, RoomTypeDTO dto);

    void delete(Long id);
}
