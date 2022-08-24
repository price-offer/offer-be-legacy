package com.prgrms.offer.authentication.application.response;

import com.prgrms.offer.domain.member.model.entity.Member.OAuthType;
import lombok.Getter;

@Getter
public class SocialProfileResponse {

    private final OAuthType oauthType;
    private final Long oauthId;
    private final String profileImageUrl;

    private SocialProfileResponse(final OAuthType oauthType, final Long oauthId, final String profileImageUrl) {
        this.oauthType = oauthType;
        this.oauthId = oauthId;
        this.profileImageUrl = profileImageUrl;
    }

    public static SocialProfileResponse of(final OAuthType oauthType, final Long oauthId, final String profileImageUrl) {
        return new SocialProfileResponse(oauthType, oauthId, profileImageUrl);
    }
}
