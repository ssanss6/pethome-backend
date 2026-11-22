package com.pethome.service;

import com.pethome.dto.PetDTO;
import com.pethome.entity.Pet;

import java.util.List;

public interface PetService {

    /** 当前宠物主的宠物列表 */
    List<Pet> listMine();

    /** 查询本人宠物详情（校验归属） */
    Pet getMine(Long id);

    Pet create(PetDTO dto);

    Pet update(Long id, PetDTO dto);

    /** 软删除 */
    void delete(Long id);
}
