package com.prgrms.offer.authentication.aop;

import com.prgrms.offer.authentication.presentation.AuthenticationContext;
import com.prgrms.offer.authentication.presentation.LoginMember.Authority;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MemberVerifier {

    private final AuthenticationContext memberAuthorityCache;

    public MemberVerifier(AuthenticationContext memberAuthorityCache) {
        this.memberAuthorityCache = memberAuthorityCache;
    }

    @Before("@annotation(com.prgrms.offer.authentication.aop.MemberOnly)")
    public void checkLoginMember() {
        final Authority authority = memberAuthorityCache.getAuthority();
        if (!authority.equals(Authority.MEMBER)) {
            throw new IllegalArgumentException("로그인 사용자만 접근 가능합니다.");
        }
    }
}
