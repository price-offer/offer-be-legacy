package com.prgrms.offer.authentication.presentation;

import com.prgrms.offer.authentication.presentation.LoginMember.Authority;
import java.util.Objects;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthenticationPrincipalArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthenticationContext authenticationContext;

    public AuthenticationPrincipalArgumentResolver(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
    }

    @Override
    public LoginMember resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                       NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        String principal = authenticationContext.getPrincipal();
        if (Objects.isNull(principal)) {
            authenticationContext.setAuthority(Authority.ANONYMOUS);
            return new LoginMember(Authority.ANONYMOUS);
        }

        authenticationContext.setAuthority(Authority.MEMBER);
        return new LoginMember(Long.valueOf(principal), Authority.MEMBER);
    }
}
