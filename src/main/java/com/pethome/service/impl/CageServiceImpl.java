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
 * 笼位服务（单门店，store_id 默认 1）
 */
@Service
@RequiredArgsConstructor
public class CageServiceImpl implements CageService {

    private static final long DEFAULT_STORE_ID = 1L;

    private final CageMapper cageMapper;

    @Override
    public List<Cage> list(Long roomTypeId) {
        LambdaQueryWrapper<Cage> qw = new LambdaQueryWrapper<>();
        if (roomTypeId != null) {
            qw.eq(Cage::getRoomTypeId, roomTypeId);
        }
        qw.orderByAsc(Cage::getCageNo);
        return cageMapper.selectList(qw);
    }

    @Override
    public Cage get(Long id) {
        Cage cage = cageMapper.selectById(id);
        if (cage == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND, "笼位不存在");
        }
        return cage;
    }

    @Override
    public Cage create(CageDTO dto) {
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

    @Override
    public Cage update(Long id, CageDTO dto) {
        Cage cage = get(id);
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

    @Override
    public void delete(Long id) {
        cageMapper.deleteById(id);
    }

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
