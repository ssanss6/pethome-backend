package com.pethome.service;

import com.pethome.dto.MembershipCardCreateDTO;
import com.pethome.entity.MembershipCard;

import java.util.List;

public interface MembershipCardService {

    MembershipCard create(MembershipCardCreateDTO dto);

    List<MembershipCard> listMine();

    /** 核销扣次 */
    void punch(Long id);
}
