package com.prgrms.offer.authentication.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.offer.authentication.application.response.KakaoAccessTokenResponse;
import com.prgrms.offer.authentication.application.response.SocialProfileResponse;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class KakaoOAuthClient {

    private static final String OAUTH_LOGIN_URL_SUFFIX = "?client_id=%s&redirect_uri=%s&response_type=code";
    private static final String TOKEN_BEARER = "Bearer ";

    private final String clientId;
    private final String clientSecret;
    private final String redirectUrl;
    private final String oauthServerUrl;
    private final String accessTokenRequestUrl;
    private final String userProfileRequestUrl;
    private final OAuthWebClient oAuthWebClient;

    public KakaoOAuthClient(@Value("${oauth2.kakao.client.id}") String clientId,
                            @Value("${oauth2.kakao.client.secret}") String clientSecret,
                            @Value("${oauth2.kakao.url.redirect}") String redirectUrl,
                            @Value("${oauth2.kakao.url.oauth-server}") String oauthServerUrl,
                            @Value("${oauth2.kakao.url.accessToken}") String accessTokenRequestUrl,
                            @Value("${oauth2.kakao.url.userProfile}") String userProfileRequestUrl,
                            OAuthWebClient oAuthWebClient) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUrl = redirectUrl;
        this.oauthServerUrl = oauthServerUrl;
        this.accessTokenRequestUrl = accessTokenRequestUrl;
        this.userProfileRequestUrl = userProfileRequestUrl;
        this.oAuthWebClient = oAuthWebClient;
    }

    public String getLoginUrl() {
        String oauthLoginUrl = oauthServerUrl + OAUTH_LOGIN_URL_SUFFIX;
        return String.format(oauthLoginUrl, clientId, redirectUrl);
    }

    public SocialProfileResponse requestSocialProfile(final String authCode) {
        String accessToken = getAccessToken(authCode);
        String socialProfileJsonString = requestSocialProfileToOAuthServer(accessToken);
        return parseToSocialProfileResponse(socialProfileJsonString);
    }

    private String requestSocialProfileToOAuthServer(final String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        headers.add(HttpHeaders.AUTHORIZATION, TOKEN_BEARER + accessToken);
        String oauthAttributes = oAuthWebClient.exchange(userProfileRequestUrl, HttpMethod.GET, new HttpEntity<>(headers),
                String.class).getBody();

        if (Objects.isNull(oauthAttributes)) {
            log.error("response body is null, request url = {}", userProfileRequestUrl);
            throw new RuntimeException("response body is null, request url =" + userProfileRequestUrl);
        }
        return oauthAttributes;
    }

    private SocialProfileResponse parseToSocialProfileResponse(final String oauthAttributes) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(oauthAttributes);
            long id = jsonNode.get("id").asLong();
            JsonNode kakaoAccount = jsonNode.get("kakao_account");
            JsonNode profile = kakaoAccount.get("profile");
            String profileImageUrl = profile.get("profile_image_url").asText();
            return SocialProfileResponse.of("kakao", id, profileImageUrl);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getAccessToken(String authCode) {
        MultiValueMap<String, String> requestParams = createRequestParam(authCode);
        return requestTokenToOAuthServer(requestParams);
    }

    private MultiValueMap<String, String> createRequestParam(final String code) {
        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.add("grant_type", "authorization_code");
        requestMap.add("client_id", clientId);
        requestMap.add("client_secret", clientSecret);
        requestMap.add("redirect_uri", redirectUrl);
        requestMap.add("code", code);
        return requestMap;
    }

    private String requestTokenToOAuthServer(final MultiValueMap<String, String> requestParams) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        HttpEntity<?> httpEntity = new HttpEntity<>(requestParams, headers);
        KakaoAccessTokenResponse response = oAuthWebClient.exchange(accessTokenRequestUrl, HttpMethod.POST, httpEntity,
                KakaoAccessTokenResponse.class).getBody();

        if (Objects.isNull(response)) {
            log.error("response body is null, request url = {}", accessTokenRequestUrl);
            throw new RuntimeException("response body is null, request url =" + accessTokenRequestUrl);
        }
        return response.getAccessToken();
    }
}
