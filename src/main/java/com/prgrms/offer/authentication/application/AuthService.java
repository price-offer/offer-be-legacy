package com.prgrms.offer.authentication.application;

import com.prgrms.offer.authentication.application.response.OAuthLoginUrlResponse;
import com.prgrms.offer.authentication.application.response.SocialProfileResponse;
import com.prgrms.offer.authentication.application.response.TokenResponse;
import com.prgrms.offer.authentication.presentation.request.TokenRequest;
import com.prgrms.offer.domain.member.model.entity.Member;
import com.prgrms.offer.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final KakaoOAuthClient kakaoOAuthClient;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RandomNicknameGenerator randomNicknameGenerator;

    public OAuthLoginUrlResponse getLoginUrl() {
        return new OAuthLoginUrlResponse(kakaoOAuthClient.getLoginUrl());
    }

    public TokenResponse createToken(TokenRequest request) {
        SocialProfileResponse socialProfile = kakaoOAuthClient.requestSocialProfile(request.getCode());
        boolean alreadyJoined = memberRepository.existsByOauthTypeAndOauthId(socialProfile.getOauthType(),
                socialProfile.getOauthId());
        Member member = findOrCreateMember(alreadyJoined, socialProfile);
        return TokenResponse.of(jwtTokenProvider.createToken(String.valueOf(member.getId())), alreadyJoined);
    }

    private Member findOrCreateMember(final boolean alreadyJoined, final SocialProfileResponse socialProfileResponse) {
        if (alreadyJoined) {
            return memberRepository.getByOauthTypeAndOauthId(socialProfileResponse.getOauthType(),
                    socialProfileResponse.getOauthId());
        }
        return createNewMember(socialProfileResponse);
    }

    private Member createNewMember(final SocialProfileResponse socialProfile) {
        String nickname = randomNicknameGenerator.generateRandomNickname();
        while (memberRepository.existsByNickname(nickname)) {
            nickname = randomNicknameGenerator.generateRandomNickname();
        }
        return memberRepository.save(Member.builder()
                .oauthId(socialProfile.getOauthId())
                .oauthType(socialProfile.getOauthType())
                .nickname(nickname)
                .profileImageUrl(socialProfile.getProfileImageUrl())
                .build());
    }
}
