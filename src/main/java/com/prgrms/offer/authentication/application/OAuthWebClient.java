package com.prgrms.offer.authentication.application;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestClientException;

public interface OAuthWebClient {

    <T> ResponseEntity<T> exchange(String url, HttpMethod method,
                                   @Nullable HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables)
            throws RestClientException;
}
