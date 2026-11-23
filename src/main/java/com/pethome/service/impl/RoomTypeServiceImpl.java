package com.pethome.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.pethome.common.exception.BizException;
import com.pethome.common.result.ErrorCode;
import com.pethome.dto.RoomTypeDTO;
import com.pethome.entity.RoomType;
import com.pethome.mapper.RoomTypeMapper;
import com.pethome.service.RedisService;
import com.pethome.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 房型服务实现（单门店，store_id 默认 1；列表走 Redis 缓存）
 */
@Service
@RequiredArgsConstructor
public class RoomTypeServiceImpl implements RoomTypeService {

    private static final long DEFAULT_STORE_ID = 1L;
    private static final String CACHE_KEY = "cache:room_type:list";

    private final RoomTypeMapper roomTypeMapper;
    private final RedisService redisService;

    /**
     * 查询启用的房型列表（Redis 缓存 10 分钟）
     *
     * @return 房型列表
     */
    @Override
    public List<RoomType> list() {
        // 先查缓存
        List<RoomType> cached = redisService.get(CACHE_KEY, new TypeReference<List<RoomType>>() {});
        if (cached != null) {
            return cached;
        }
        // 未命中查库并写缓存
        List<RoomType> list = roomTypeMapper.selectList(new LambdaQueryWrapper<RoomType>()
                .eq(RoomType::getStatus, 1)
                .orderByAsc(RoomType::getId));
        redisService.set(CACHE_KEY, list, 10, TimeUnit.MINUTES);
        return list;
    }

    /**
     * 根据id查询房型
     *
     * @param id 房型ID
     * @return 房型实体
     */
    @Override
    public RoomType get(Long id) {
        RoomType roomType = roomTypeMapper.selectById(id);
        if (roomType == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND, "房型不存在");
        }
        return roomType;
    }

    /**
     * 新增房型
     *
     * @param dto 房型信息
     * @return 新增后的房型
     */
    @Override
    public RoomType create(RoomTypeDTO dto) {
        // dto 转实体，补齐门店与状态
        RoomType roomType = new RoomType();
        BeanUtil.copyProperties(dto, roomType);
        roomType.setStoreId(DEFAULT_STORE_ID);
        roomType.setStatus(1);
        roomTypeMapper.insert(roomType);
        redisService.delete(CACHE_KEY);
        return roomType;
    }

    /**
     * 编辑房型（仅更新非空字段）
     *
     * @param id  房型ID
     * @param dto 房型信息
     * @return 更新后的房型
     */
    @Override
    public RoomType update(Long id, RoomTypeDTO dto) {
        RoomType roomType = get(id);
        BeanUtil.copyProperties(dto, roomType, CopyOptions.create().setIgnoreNullValue(true));
        roomTypeMapper.updateById(roomType);
        redisService.delete(CACHE_KEY);
        return roomType;
    }

    /**
     * 删除房型
     *
     * @param id 房型ID
     */
    @Override
    public void delete(Long id) {
        roomTypeMapper.deleteById(id);
        redisService.delete(CACHE_KEY);
    }
}
