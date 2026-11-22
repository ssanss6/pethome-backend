package com.pethome.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pethome.common.exception.BizException;
import com.pethome.common.result.ErrorCode;
import com.pethome.dto.CageDTO;
import com.pethome.entity.Cage;
import com.pethome.mapper.CageMapper;
import com.pethome.service.CageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 笼位服务实现（单门店，store_id 默认 1）
 */
@Service
@RequiredArgsConstructor
public class CageServiceImpl implements CageService {

    private static final long DEFAULT_STORE_ID = 1L;

    private final CageMapper cageMapper;

    /**
     * 查询笼位列表（可按房型筛选）
     *
     * @param roomTypeId 房型ID（可选）
     * @return 笼位列表
     */
    @Override
    public List<Cage> list(Long roomTypeId) {
        LambdaQueryWrapper<Cage> qw = new LambdaQueryWrapper<>();
        if (roomTypeId != null) {
            qw.eq(Cage::getRoomTypeId, roomTypeId);
        }
        qw.orderByAsc(Cage::getCageNo);
        return cageMapper.selectList(qw);
    }

    /**
     * 根据id查询笼位
     *
     * @param id 笼位ID
     * @return 笼位实体
     */
    @Override
    public Cage get(Long id) {
        Cage cage = cageMapper.selectById(id);
        if (cage == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND, "笼位不存在");
        }
        return cage;
    }

    /**
     * 新增笼位
     *
     * @param dto 笼位信息
     * @return 新增后的笼位
     */
    @Override
    public Cage create(CageDTO dto) {
        // 笼位号同门店内唯一
        if (existsCageNo(dto.getCageNo(), null)) {
            throw new BizException(ErrorCode.DUPLICATE_SUBMIT, "笼位号已存在");
        }
        Cage cage = new Cage();
        cage.setStoreId(DEFAULT_STORE_ID);
        cage.setRoomTypeId(dto.getRoomTypeId());
        cage.setCageNo(dto.getCageNo());
        cage.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        cageMapper.insert(cage);
        return cage;
    }

    /**
     * 编辑笼位（仅更新非空字段）
     *
     * @param id  笼位ID
     * @param dto 笼位信息
     * @return 更新后的笼位
     */
    @Override
    public Cage update(Long id, CageDTO dto) {
        Cage cage = get(id);
        // 改笼位号时校验唯一
        if (dto.getCageNo() != null && !dto.getCageNo().equals(cage.getCageNo())
                && existsCageNo(dto.getCageNo(), id)) {
            throw new BizException(ErrorCode.DUPLICATE_SUBMIT, "笼位号已存在");
        }
        if (dto.getRoomTypeId() != null) {
            cage.setRoomTypeId(dto.getRoomTypeId());
        }
        if (dto.getCageNo() != null) {
            cage.setCageNo(dto.getCageNo());
        }
        if (dto.getStatus() != null) {
            cage.setStatus(dto.getStatus());
        }
        cageMapper.updateById(cage);
        return cage;
    }

    /**
     * 删除笼位
     *
     * @param id 笼位ID
     */
    @Override
    public void delete(Long id) {
        cageMapper.deleteById(id);
    }

    /**
     * 判断笼位号是否已存在
     *
     * @param cageNo    笼位号
     * @param excludeId 排除的笼位ID（编辑时排除自身）
     * @return true 已存在
     */
    private boolean existsCageNo(String cageNo, Long excludeId) {
        LambdaQueryWrapper<Cage> qw = new LambdaQueryWrapper<Cage>()
                .eq(Cage::getStoreId, DEFAULT_STORE_ID)
                .eq(Cage::getCageNo, cageNo);
        if (excludeId != null) {
            qw.ne(Cage::getId, excludeId);
        }
        return cageMapper.selectCount(qw) > 0;
    }
}
