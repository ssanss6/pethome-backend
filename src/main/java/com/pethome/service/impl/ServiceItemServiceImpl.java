package com.pethome.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pethome.common.exception.BizException;
import com.pethome.common.result.ErrorCode;
import com.pethome.dto.ServiceItemDTO;
import com.pethome.entity.ServiceItem;
import com.pethome.mapper.ServiceItemMapper;
import com.pethome.service.ServiceItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 洗护服务项目实现
 */
@Service
@RequiredArgsConstructor
public class ServiceItemServiceImpl implements ServiceItemService {

    private final ServiceItemMapper serviceItemMapper;

    @Override
    public List<ServiceItem> list() {
        return serviceItemMapper.selectList(new LambdaQueryWrapper<ServiceItem>()
                .eq(ServiceItem::getStatus, 1)
                .orderByAsc(ServiceItem::getId));
    }

    @Override
    public ServiceItem create(ServiceItemDTO dto) {
        ServiceItem item = new ServiceItem();
        BeanUtil.copyProperties(dto, item);
        item.setStoreId(1L);
        item.setStatus(1);
        serviceItemMapper.insert(item);
        return item;
    }

    @Override
    public ServiceItem update(Long id, ServiceItemDTO dto) {
        ServiceItem item = serviceItemMapper.selectById(id);
        if (item == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND, "服务项目不存在");
        }
        BeanUtil.copyProperties(dto, item, CopyOptions.create().setIgnoreNullValue(true));
        serviceItemMapper.updateById(item);
        return item;
    }

    @Override
    public void delete(Long id) {
        serviceItemMapper.deleteById(id);
    }
}
