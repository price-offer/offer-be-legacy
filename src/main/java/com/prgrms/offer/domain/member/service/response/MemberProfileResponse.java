package com.prgrms.offer.domain.member.service.response;

import com.prgrms.offer.domain.member.model.entity.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberProfileResponse {

    private final Long id;
    private final String nickname;
    private final int offerLevel;
    private final String profileImageUrl;

    public static MemberProfileResponse from(Member member) {
        return new MemberProfileResponse(member.getId(), member.getNickname(), member.getOfferLevel(),
                member.getProfileImageUrl());
    }
}
