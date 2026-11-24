package com.pethome.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pethome.common.exception.BizException;
import com.pethome.common.result.ErrorCode;
import com.pethome.dto.GroomerDTO;
import com.pethome.entity.Groomer;
import com.pethome.mapper.GroomerMapper;
import com.pethome.service.GroomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 美容师服务实现
 */
@Service
@RequiredArgsConstructor
public class GroomerServiceImpl implements GroomerService {

    private final GroomerMapper groomerMapper;

    @Override
    public List<Groomer> list() {
        return groomerMapper.selectList(new LambdaQueryWrapper<Groomer>()
                .eq(Groomer::getStatus, 1)
                .orderByAsc(Groomer::getId));
    }

    @Override
    public Groomer create(GroomerDTO dto) {
        Groomer groomer = new Groomer();
        BeanUtil.copyProperties(dto, groomer);
        groomer.setStoreId(1L);
        groomer.setStatus(1);
        groomerMapper.insert(groomer);
        return groomer;
    }

    @Override
    public Groomer update(Long id, GroomerDTO dto) {
        Groomer groomer = groomerMapper.selectById(id);
        if (groomer == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND, "美容师不存在");
        }
        BeanUtil.copyProperties(dto, groomer, CopyOptions.create().setIgnoreNullValue(true));
        groomerMapper.updateById(groomer);
        return groomer;
    }

    @Override
    public void delete(Long id) {
        groomerMapper.deleteById(id);
    }
}
