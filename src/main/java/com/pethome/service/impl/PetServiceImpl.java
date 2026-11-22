package com.pethome.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pethome.common.exception.BizException;
import com.pethome.common.result.ErrorCode;
import com.pethome.dto.PetDTO;
import com.pethome.entity.Pet;
import com.pethome.mapper.PetMapper;
import com.pethome.security.UserContext;
import com.pethome.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 宠物档案服务
 */
@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {

    private final PetMapper petMapper;

    @Override
    public List<Pet> listMine() {
        return petMapper.selectList(new LambdaQueryWrapper<Pet>()
                .eq(Pet::getOwnerId, UserContext.getUserId())
                .eq(Pet::getStatus, 1)
                .orderByDesc(Pet::getId));
    }

    @Override
    public Pet getMine(Long id) {
        Pet pet = petMapper.selectOne(new LambdaQueryWrapper<Pet>()
                .eq(Pet::getId, id)
                .eq(Pet::getOwnerId, UserContext.getUserId())
                .eq(Pet::getStatus, 1));
        if (pet == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND, "宠物不存在");
        }
        return pet;
    }

    @Override
    public Pet create(PetDTO dto) {
        Pet pet = new Pet();
        BeanUtil.copyProperties(dto, pet);
        pet.setOwnerId(UserContext.getUserId());
        pet.setStatus(1);
        petMapper.insert(pet);
        return pet;
    }

    @Override
    public Pet update(Long id, PetDTO dto) {
        Pet pet = getMine(id);
        // 仅更新非空字段（局部更新）
        BeanUtil.copyProperties(dto, pet, CopyOptions.create().setIgnoreNullValue(true));
        petMapper.updateById(pet);
        return pet;
    }

    @Override
    public void delete(Long id) {
        Pet pet = getMine(id);
        pet.setStatus(0);
        petMapper.updateById(pet);
    }
}
