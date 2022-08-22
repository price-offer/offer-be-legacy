package com.prgrms.offer.authentication.application.response;

import lombok.Getter;

@Getter
public class SocialProfileResponse {

    private final String oauthType;
    private final Long oauthId;
    private final String profileImageUrl;

    private SocialProfileResponse(final String oauthType, final Long oauthId, final String profileImageUrl) {
        this.oauthType = oauthType;
        this.oauthId = oauthId;
        this.profileImageUrl = profileImageUrl;
    }

    public static SocialProfileResponse of(final String oauthType, final Long oauthId, final String profileImageUrl) {
        return new SocialProfileResponse(oauthType, oauthId, profileImageUrl);
    }
}
