package com.pethome.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pethome.common.exception.BizException;
import com.pethome.common.result.ErrorCode;
import com.pethome.dto.RoomTypeDTO;
import com.pethome.entity.RoomType;
import com.pethome.mapper.RoomTypeMapper;
import com.pethome.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 房型服务（单门店，store_id 默认 1）
 */
@Service
@RequiredArgsConstructor
public class RoomTypeServiceImpl implements RoomTypeService {

    private static final long DEFAULT_STORE_ID = 1L;

    private final RoomTypeMapper roomTypeMapper;

    @Override
    public List<RoomType> list() {
        return roomTypeMapper.selectList(new LambdaQueryWrapper<RoomType>()
                .eq(RoomType::getStatus, 1)
                .orderByAsc(RoomType::getId));
    }

    @Override
    public RoomType get(Long id) {
        RoomType roomType = roomTypeMapper.selectById(id);
        if (roomType == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND, "房型不存在");
        }
        return roomType;
    }

    @Override
    public RoomType create(RoomTypeDTO dto) {
        RoomType roomType = new RoomType();
        BeanUtil.copyProperties(dto, roomType);
        roomType.setStoreId(DEFAULT_STORE_ID);
        roomType.setStatus(1);
        roomTypeMapper.insert(roomType);
        return roomType;
    }

    @Override
    public RoomType update(Long id, RoomTypeDTO dto) {
        RoomType roomType = get(id);
        BeanUtil.copyProperties(dto, roomType, CopyOptions.create().setIgnoreNullValue(true));
        roomTypeMapper.updateById(roomType);
        return roomType;
    }

    @Override
    public void delete(Long id) {
        roomTypeMapper.deleteById(id);
    }
}
