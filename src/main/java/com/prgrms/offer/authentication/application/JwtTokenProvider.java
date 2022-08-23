package com.prgrms.offer.authentication.application;

public interface JwtTokenProvider {
    String createToken(final String subject);

    String extractSubject(final String token) throws IllegalArgumentException;

}
