package com.prgrms.offer.domain.member.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberResponse {

    private final MemberDto member;

    @Getter
    public static class MemberDto {
        private final Long id;
        private final String email;
        private final int offerLevel;
        private final String nickname;
        private final String profileImageUrl;
        private final String address;

        @Builder
        public MemberDto(Long id, String email, int offerLevel, String nickname, String profileImageUrl, String address) {
            this.id = id;
            this.email = email;
            this.offerLevel = offerLevel;
            this.nickname = nickname;
            this.profileImageUrl = profileImageUrl;
            this.address = address;
        }

        @Override
        public String toString() {
            return "MemberDto{" +
                    "id=" + id +
                    ", email='" + email + '\'' +
                    ", offerLevel=" + offerLevel +
                    ", nickname='" + nickname + '\'' +
                    ", profileImageUrl='" + profileImageUrl + '\'' +
                    ", address='" + address + '\'' +
                    '}';
        }
    }

    public MemberResponse(MemberDto member) {
        this.member = member;
    }

    @Override
    public String toString() {
        return "MemberResponse{" +
                "member=" + member +
                '}';
    }
}
