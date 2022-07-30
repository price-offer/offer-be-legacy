package com.prgrms.offer.authentication.presentation;

public class LoginMember {

    public enum Authority {
        ANONYMOUS, MEMBER
    }

    private Long id;
    private Authority authority;

    public LoginMember(Authority authority) {
        this(null, authority);
    }

    public LoginMember(Long id, Authority authority) {
        this.id = id;
        this.authority = authority;
    }

    public boolean isAnonymous() {
        return Authority.ANONYMOUS.equals(authority);
    }

    public boolean isMember() {
        return Authority.MEMBER.equals(authority);
    }

    public Long getId() {
        return id;
    }
}
