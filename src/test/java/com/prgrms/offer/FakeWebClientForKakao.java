package com.prgrms.offer;

import com.prgrms.offer.authentication.application.OAuthWebClient;
import com.prgrms.offer.authentication.application.response.KakaoAccessTokenResponse;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

/**
 * 카카오 인증 서버에 정해진 스펙대로 요청을 보내는지 테스트하기 위한 대역
 **/
public class FakeWebClientForKakao implements OAuthWebClient {

    public static final String ACCESS_TOKEN = "access_token";
    private final String clientId;
    private final String clientSecret;
    private final String redirectUrl;
    private final String oauthServerUrl;
    private final String accessTokenRequestUrl;
    private final String userProfileRequestUrl;

    public FakeWebClientForKakao(final String clientId, final String clientSecret, final String redirectUrl,
                                 final String oauthServerUrl,
                                 final String accessTokenRequestUrl, final String userProfileRequestUrl) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUrl = redirectUrl;
        this.oauthServerUrl = oauthServerUrl;
        this.accessTokenRequestUrl = accessTokenRequestUrl;
        this.userProfileRequestUrl = userProfileRequestUrl;
    }

    @Override
    public <T> ResponseEntity<T> exchange(final String url, final HttpMethod method, final HttpEntity<?> requestEntity,
                                          final Class<T> responseType, final Object... uriVariables)
            throws RestClientException {

        /*
          엑세스 토큰 요청 스펙
          method: POST
          Content-Type: application/x-www-form-urlencoded
          URL: https://test-kakao-access-token-url
          Request Params:
          { grant_type : authorization_code,
            client_id : test-client-id,
            redirect_uri : http://test-redirect-url
            code : 임의의 문자열
            client_secret : test-client-secret
          }
          */
        if (method.matches(HttpMethod.POST.name())) {
            if (!accessTokenRequestUrl.equals(url)) {
                throw new RuntimeException("token request url is invalid. url = " + url);
            }
            validateContentType(requestEntity);
            validateTokenRequestBody(requestEntity);
            KakaoAccessTokenResponse response = new KakaoAccessTokenResponse(ACCESS_TOKEN);
            return new ResponseEntity<>((T) response, HttpStatus.OK);
        }

        /*
          엑세스 토큰 요청 스펙
          method: GET
          Content-Type: application/x-www-form-urlencoded
          URL: https://test-kakao-access-token-url
          Request Params:
          { grant_type : authorization_code,
            client_id : test-client-id,
            redirect_uri : http://test-redirect-url
            code : 임의의 문자열
            client_secret : test-client-secret
          }
          */
        if (method.matches(HttpMethod.GET.name())) {
            if (!userProfileRequestUrl.equals(url)) {
                throw new RuntimeException("userProfileRequestUrl is invalid. url = " + url);
            }
            validateContentType(requestEntity);
            validateAccessTokenExists(requestEntity);

            String testUserProfile = "{\n"
                    + "    \"id\":1234,\n"
                    + "    \"kakao_account\": {\n"
                    + "        \"profile_nickname_needs_agreement    \": false,\n"
                    + "        \"profile_image_needs_agreement    \": false,\n"
                    + "        \"profile\": {\n"
                    + "            \"nickname\": \"홍길동\",\n"
                    + "            \"thumbnail_image_url\": \"http://yyy.kakao.com/.../img_110x110.jpg\",\n"
                    + "            \"profile_image_url\": \"http://test.jpg\",\n"
                    + "            \"is_default_image\":false\n"
                    + "        },\n"
                    + "        \"name_needs_agreement\":false, \n"
                    + "        \"name\":\"홍길동\"\n"
                    + "    }\n"
                    + "}";
            return new ResponseEntity<>((T) testUserProfile, HttpStatus.OK);
        }

        throw new RuntimeException("Invalid HTTP method");
    }

    private void validateAccessTokenExists(final HttpEntity<?> requestEntity) {
        HttpHeaders headers = requestEntity.getHeaders();
        List<String> authorization = headers.get(HttpHeaders.AUTHORIZATION);
        if (Objects.isNull(authorization) || authorization.isEmpty() || !authorization.get(0).contains(ACCESS_TOKEN)) {
            throw new RuntimeException("access token doesn't exist");
        }
    }

    private void validateTokenRequestBody(final HttpEntity<?> requestEntity) {
        MultiValueMap<String, String> body = (MultiValueMap<String, String>) requestEntity.getBody();
        if (Objects.isNull(body)) {
            throw new RuntimeException("body is null");
        }
        List<String> grant_type = body.get("grant_type");
        List<String> client_id = body.get("client_id");
        List<String> client_secret = body.get("client_secret");
        List<String> redirect_uri = body.get("redirect_uri");
        List<String> code = body.get("code");

        if (!grant_type.contains("authorization_code")) {
            throw new RuntimeException("invalid grant_type = " + grant_type);
        }

        if (!client_id.contains(this.clientId)) {
            throw new RuntimeException("invalid client_id = " + client_id);
        }

        if (!client_secret.contains(this.clientSecret)) {
            throw new RuntimeException("invalid client_secret = " + client_secret);
        }

        if (!redirect_uri.contains(this.redirectUrl)) {
            throw new RuntimeException("invalid redirect_uri = " + redirect_uri);
        }

        if (code.isEmpty()) {
            throw new RuntimeException("auth code is empty");
        }
    }

    private void validateContentType(final HttpEntity<?> requestEntity) {
        HttpHeaders headers = requestEntity.getHeaders();
        MediaType contentType = headers.getContentType();
        if (Objects.isNull(contentType)) {
            throw new RuntimeException("Content-Type is null");
        }
        if (!contentType.isCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED)) {
            throw new RuntimeException(
                    "Content-Type in not compatible with application/x-www-form-urlencoded. Content-Type = "
                            + contentType);
        }
    }
}
