package com.konggogi.veganlife.global.security.user;


import com.konggogi.veganlife.member.domain.Role;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public record JwtUserPrincipal(Long id) implements Serializable {

    public JwtUserPrincipal(String id) {
        this(Long.parseLong(id));
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(new SimpleGrantedAuthority(Role.USER.name()));
    }
}
