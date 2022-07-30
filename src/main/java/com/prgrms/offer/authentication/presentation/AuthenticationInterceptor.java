package com.prgrms.offer.authentication.presentation;

import com.prgrms.offer.authentication.application.JwtTokenProvider;
import com.prgrms.offer.common.utils.AuthorizationTokenExtractor;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationContext authenticationContext;

    public AuthenticationInterceptor(final JwtTokenProvider jwtTokenProvider,
                                     final AuthenticationContext authenticationContext) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationContext = authenticationContext;
    }

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) {
        if (CorsUtils.isPreFlightRequest(request)) {
            return true;
        }

        String token = AuthorizationTokenExtractor.extractToken(request);
        if (Objects.nonNull(token)) {
            String subject = jwtTokenProvider.extractSubject(token);
            authenticationContext.setPrincipal(subject);
        }
        return true;
    }
}
