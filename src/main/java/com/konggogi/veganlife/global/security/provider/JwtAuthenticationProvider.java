package com.konggogi.veganlife.global.security.provider;


import com.konggogi.veganlife.global.security.jwt.JwtPreAuthenticationToken;
import com.konggogi.veganlife.global.security.jwt.JwtUserAuthenticationToken;
import com.konggogi.veganlife.global.security.jwt.JwtUtils;
import com.konggogi.veganlife.global.security.user.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        /** authentication이 JwtPreAuthenticationToken이라면 authenticate 진행 */
        if (!supports(authentication.getClass())) {
            return null;
        }
        JwtUserPrincipal principal =
                JwtUtils.getPrincipal(authentication.getPrincipal().toString());
        return new JwtUserAuthenticationToken(principal);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication == JwtPreAuthenticationToken.class;
    }
}
