package com.prgrms.offer.authentication.presentation;

import com.prgrms.offer.authentication.presentation.LoginMember.Authority;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@Getter
@Setter
public class AuthenticationContext {

    private String principal;
    private Authority authority;
}
