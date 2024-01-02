package com.konggogi.veganlife.global.security.jwt;


import com.konggogi.veganlife.global.security.user.JwtUserPrincipal;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JwtUserAuthenticationToken extends AbstractAuthenticationToken {

    private final JwtUserPrincipal principal;

    public JwtUserAuthenticationToken(JwtUserPrincipal principal) {
        super(principal.getAuthorities());
        super.setAuthenticated(true);
        this.principal = principal;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return null;
    }
}
