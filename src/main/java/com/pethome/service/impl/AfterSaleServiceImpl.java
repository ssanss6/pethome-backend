package com.pethome.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pethome.common.exception.BizException;
import com.pethome.common.result.ErrorCode;
import com.pethome.dto.AfterSaleCreateDTO;
import com.pethome.entity.AfterSale;
import com.pethome.enums.AfterSaleStatus;
import com.pethome.mapper.AfterSaleMapper;
import com.pethome.security.UserContext;
import com.pethome.service.AfterSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 售后工单服务实现
 */
@Service
@RequiredArgsConstructor
public class AfterSaleServiceImpl implements AfterSaleService {

    private final AfterSaleMapper afterSaleMapper;

    @Override
    public AfterSale create(AfterSaleCreateDTO dto) {
        AfterSale as = new AfterSale();
        as.setOrderType(dto.getOrderType());
        as.setOrderId(dto.getOrderId());
        as.setOwnerId(UserContext.getUserId());
        as.setType(dto.getType());
        as.setReason(dto.getReason());
        as.setStatus(AfterSaleStatus.OPEN.getCode());
        afterSaleMapper.insert(as);
        return as;
    }

    @Override
    public List<AfterSale> listMine() {
        return afterSaleMapper.selectList(new LambdaQueryWrapper<AfterSale>()
                .eq(AfterSale::getOwnerId, UserContext.getUserId())
                .orderByDesc(AfterSale::getId));
    }

    @Override
    public List<AfterSale> listAll() {
        return afterSaleMapper.selectList(new LambdaQueryWrapper<AfterSale>()
                .orderByDesc(AfterSale::getId));
    }

    @Override
    public void handle(Long id, String status) {
        AfterSale as = afterSaleMapper.selectById(id);
        if (as == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND, "工单不存在");
        }
        as.setStatus(status);
        afterSaleMapper.updateById(as);
    }
}
