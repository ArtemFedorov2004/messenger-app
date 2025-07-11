package io.github.artemfedorov2004.messengerserver.service;

import io.github.artemfedorov2004.messengerserver.entity.Role;
import io.github.artemfedorov2004.messengerserver.entity.User;
import io.github.artemfedorov2004.messengerserver.exception.AlreadyExistsException;
import io.github.artemfedorov2004.messengerserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User createUser(String username, String email, String password) {
        if (this.userRepository.existsByUsername(username)) {
            throw new AlreadyExistsException("messenger_server.users.create.errors.username_already_taken");
        }
        if (this.userRepository.existsByEmail(email)) {
            throw new AlreadyExistsException("messenger_server.users.create.errors.email_already_taken");
        }

        var user = User.builder()
                .username(username)
                .email(email)
                .password(this.passwordEncoder.encode(password))
                .role(Role.ROLE_USER)
                .build();

        return this.userRepository.save(user);
    }

    @Override
    public User getByUsername(String username) {
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "messenger_server.errors.user_not_found"));
    }

    @Override
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    @Override
    public Page<User> searchUsersExcludingCurrent(String query, User currentUser, Pageable pageable) {
        return this.userRepository.findByUsernameContainingOrEmailContainingAllIgnoreCaseExcludingUsername(
                query, query, currentUser.getUsername(), pageable);
    }

    @Override
    public boolean existsAllByUsernames(Collection<String> usernames) {
        long uniqueCount = usernames.stream().distinct().count();
        int existingCount = this.userRepository.countAllByUsernames(usernames);
        return uniqueCount == existingCount;
    }
}
