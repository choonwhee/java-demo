package com.demo.test.common.security;

import com.demo.test.common.user.dto.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimpleUserDetails implements org.springframework.security.core.userdetails.UserDetails {

    private User user;
    private List<GrantedAuthority> authorities;

    public SimpleUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authorities == null) {
            authorities = new ArrayList<>();
            List<String> roles = user.getRoles();
            if (roles != null && roles.size() > 0) {
                roles.forEach(role -> {
                    authorities.add(new SimpleGrantedAuthority(role));
                });
            }
        }
        return authorities;
    }

    public User getUser() { return user; }

    @Override
    public String getUsername() {
        return user.getId();
    }

    @Override
    public String getPassword() {
        return "{noop}" + user.getPassword();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
