package com.prgrms.offer.domain.member.service;

import com.prgrms.offer.domain.member.model.dto.MemberProfile;
import com.prgrms.offer.domain.member.model.dto.MemberResponse;
import com.prgrms.offer.domain.member.model.dto.MyProfile;
import com.prgrms.offer.domain.member.model.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberConverter {

    public MemberResponse toMemberResponse(Member member) {
        MemberResponse.MemberDto memberDto = MemberResponse.MemberDto.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .offerLevel(member.getOfferLevel())
                .address(member.getAddress())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
        return new MemberResponse(memberDto);
    }

    public MemberProfile toMemberProfile(Member member, long articleCount, long reviewCount) {
        MemberProfile.MemberDto memberDto = MemberProfile.MemberDto.builder()
                .id(member.getId())
                .offerLevel(member.getOfferLevel())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .address(member.getAddress())
                .build();
        return new MemberProfile(memberDto, articleCount, reviewCount);
    }

    public MyProfile toMyProfile(Member member, long articleCount, long likeCount, long offerCount, long reviewCount) {
        MyProfile.MemberDto memberDto = MyProfile.MemberDto.builder()
                .id(member.getId())
                .offerLevel(member.getOfferLevel())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .address(member.getAddress())
                .build();
        return new MyProfile(memberDto, articleCount, likeCount, offerCount, reviewCount);
    }
}
