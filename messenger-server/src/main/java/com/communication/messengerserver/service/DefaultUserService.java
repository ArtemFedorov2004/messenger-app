package com.communication.messengerserver.service;

import com.communication.messengerserver.entity.Role;
import com.communication.messengerserver.entity.User;
import com.communication.messengerserver.exception.AlreadyExistsException;
import com.communication.messengerserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ChatService chatService;

    @Override
    public User createUser(String username, String email, String password) {
        if (this.userRepository.existsByUsername(username)) {
            throw new AlreadyExistsException("Username '%s' is already taken.".formatted(username));
        }

        if (this.userRepository.existsByEmail(email)) {
            throw new AlreadyExistsException("Email '%s' is already in use.".formatted(email));
        }

        var user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(Role.ROLE_USER)
                .build();


        User saved = this.userRepository.save(user);
        this.createChatsForNewUser(saved);

        return saved;
    }

    @Override
    public User getByUsername(String username) {
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User with username '%s' not found".formatted(username)));
    }

    @Override
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    @Override
    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return this.getByUsername(username);
    }

    // этой логики не должно быть сдесь
    private void createChatsForNewUser(User user) {
        this.chatService.createChatForNewUser(user);
    }
}
