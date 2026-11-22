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
 * 宠物档案服务实现
 */
@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {

    private final PetMapper petMapper;

    /**
     * 查询当前宠物主的宠物列表
     *
     * @return 宠物列表
     */
    @Override
    public List<Pet> listMine() {
        // 按当前登录用户 owner_id 过滤，且只查未删除的
        return petMapper.selectList(new LambdaQueryWrapper<Pet>()
                .eq(Pet::getOwnerId, UserContext.getUserId())
                .eq(Pet::getStatus, 1)
                .orderByDesc(Pet::getId));
    }

    /**
     * 查询本人宠物详情（校验归属）
     *
     * @param id 宠物ID
     * @return 宠物实体
     */
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

    /**
     * 新增宠物
     *
     * @param dto 宠物信息
     * @return 新增后的宠物
     */
    @Override
    public Pet create(PetDTO dto) {
        // 需将 dto 转成实体，并补充归属人
        Pet pet = new Pet();
        BeanUtil.copyProperties(dto, pet);
        pet.setOwnerId(UserContext.getUserId());
        pet.setStatus(1);
        petMapper.insert(pet);
        return pet;
    }

    /**
     * 编辑宠物（仅更新非空字段）
     *
     * @param id  宠物ID
     * @param dto 宠物信息
     * @return 更新后的宠物
     */
    @Override
    public Pet update(Long id, PetDTO dto) {
        // 先校验归属，再局部更新非空字段
        Pet pet = getMine(id);
        BeanUtil.copyProperties(dto, pet, CopyOptions.create().setIgnoreNullValue(true));
        petMapper.updateById(pet);
        return pet;
    }

    /**
     * 删除宠物（软删除）
     *
     * @param id 宠物ID
     */
    @Override
    public void delete(Long id) {
        // 软删除：置 status = 0
        Pet pet = getMine(id);
        pet.setStatus(0);
        petMapper.updateById(pet);
    }
}
