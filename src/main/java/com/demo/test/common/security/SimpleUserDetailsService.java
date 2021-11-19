package com.demo.test.common.security;

import com.demo.test.common.user.dto.User;
import com.demo.test.common.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class SimpleUserDetailsService implements UserDetailsService {
    Logger logger = LoggerFactory.getLogger(SimpleUserDetailsService.class);

    private UserService service;

    public SimpleUserDetailsService(UserService service) {
        this.service = service;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Username: " + username);
        Optional<User> user = service.findById(username);
        logger.info("Password: " + user.get().getPassword());
        if (!user.isPresent()) throw new UsernameNotFoundException("User (" + username + ") not found!");
        return new SimpleUserDetails(user.get());
    }
}
