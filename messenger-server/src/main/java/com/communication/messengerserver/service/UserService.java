package com.communication.messengerserver.service;

import com.communication.messengerserver.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {

    User createUser(String username, String email, String password);

    User getByUsername(String username);

    UserDetailsService userDetailsService();

    User getCurrentUser();
}
