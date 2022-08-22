package com.prgrms.offer.authentication.application;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class KakaoWebClient implements OAuthWebClient{

    private final RestTemplate restTemplate;

    @Override
    public <T> ResponseEntity<T> exchange(final String url, final HttpMethod method, final HttpEntity<?> requestEntity,
                                          final Class<T> responseType, final Object... uriVariables)
            throws RestClientException {
        return restTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
    }
}
