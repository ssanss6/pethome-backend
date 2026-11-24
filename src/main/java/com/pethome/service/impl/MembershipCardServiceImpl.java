package com.pethome.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pethome.common.exception.BizException;
import com.pethome.common.result.ErrorCode;
import com.pethome.dto.MembershipCardCreateDTO;
import com.pethome.entity.MembershipCard;
import com.pethome.entity.ServiceItem;
import com.pethome.mapper.MembershipCardMapper;
import com.pethome.mapper.ServiceItemMapper;
import com.pethome.security.UserContext;
import com.pethome.service.MembershipCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 会员次卡服务实现
 */
@Service
@RequiredArgsConstructor
public class MembershipCardServiceImpl implements MembershipCardService {

    private final MembershipCardMapper membershipCardMapper;
    private final ServiceItemMapper serviceItemMapper;

    @Override
    public MembershipCard create(MembershipCardCreateDTO dto) {
        if (dto.getServiceItemId() == null || dto.getTotalTimes() == null || dto.getTotalTimes() <= 0) {
            throw new BizException(ErrorCode.PARAM_ERROR, "次卡信息不完整");
        }
        ServiceItem item = serviceItemMapper.selectById(dto.getServiceItemId());
        if (item == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND, "服务项目不存在");
        }
        MembershipCard card = new MembershipCard();
        card.setOwnerId(UserContext.getUserId());
        card.setServiceItemId(dto.getServiceItemId());
        card.setTotalTimes(dto.getTotalTimes());
        card.setUsedTimes(0);
        card.setExpireDate(dto.getExpireDate());
        card.setStatus(1);
        membershipCardMapper.insert(card);
        return card;
    }

    @Override
    public List<MembershipCard> listMine() {
        return membershipCardMapper.selectList(new LambdaQueryWrapper<MembershipCard>()
                .eq(MembershipCard::getOwnerId, UserContext.getUserId())
                .orderByDesc(MembershipCard::getId));
    }

    @Override
    public void punch(Long id) {
        MembershipCard card = membershipCardMapper.selectById(id);
        if (card == null || card.getStatus() != 1) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND, "次卡不存在或已失效");
        }
        if (card.getUsedTimes() >= card.getTotalTimes()) {
            throw new BizException(ErrorCode.BALANCE_NOT_ENOUGH, "次卡次数已用完");
        }
        card.setUsedTimes(card.getUsedTimes() + 1);
        membershipCardMapper.updateById(card);
    }
}
