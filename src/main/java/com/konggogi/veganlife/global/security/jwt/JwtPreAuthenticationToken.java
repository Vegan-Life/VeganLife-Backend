package com.konggogi.veganlife.global.security.jwt;


import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

public class JwtPreAuthenticationToken extends AbstractAuthenticationToken {
    private final String token;

    public JwtPreAuthenticationToken(String token) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.token = token;
    }

    public static JwtPreAuthenticationToken getEmptyToken() {
        return new JwtPreAuthenticationToken("");
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return null;
    }
}
