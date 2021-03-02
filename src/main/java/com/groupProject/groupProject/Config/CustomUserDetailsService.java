package com.groupProject.groupProject.Config;

import com.groupProject.groupProject.Service.UserService;
import com.groupProject.groupProject.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserService userService;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity = userService.findByEmail(username);
        return CustomUserDetails.fromUserEntityToCustomUserDetails(userEntity);
    }
}
