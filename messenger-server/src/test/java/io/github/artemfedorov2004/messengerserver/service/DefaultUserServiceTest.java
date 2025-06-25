package io.github.artemfedorov2004.messengerserver.service;

import io.github.artemfedorov2004.messengerserver.entity.Role;
import io.github.artemfedorov2004.messengerserver.entity.User;
import io.github.artemfedorov2004.messengerserver.exception.AlreadyExistsException;
import io.github.artemfedorov2004.messengerserver.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultUserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    DefaultUserService service;

    @Test
    void createUser_WithNewUsernameAndEmail_ReturnsCreatedUser() {
        // given
        String username = "newuser";
        String email = "newuser@example.com";
        String password = "password";

        doReturn(false)
                .when(this.userRepository).existsByUsername(username);
        doReturn(false)
                .when(this.userRepository).existsByEmail(email);
        doReturn("encoded")
                .when(this.passwordEncoder).encode(password);

        doReturn(new User(username, email, "encoded", Role.ROLE_USER))
                .when(this.userRepository).save(new User(username, email, "encoded", Role.ROLE_USER));

        // when
        User result = this.service.createUser(username, email, password);

        // then
        assertEquals(new User(username, email, "encoded", Role.ROLE_USER), result);

        verify(this.userRepository).existsByUsername(username);
        verify(this.userRepository).existsByEmail(email);
        verify(this.passwordEncoder).encode(password);
        verify(this.userRepository).save(new User(username, email, "encoded", Role.ROLE_USER));
        verifyNoMoreInteractions(this.userRepository, this.passwordEncoder);
    }

    @Test
    void createUser_WithExistingUsername_ThrowsAlreadyExistsException() {
        // given
        String username = "existinguser";
        String email = "new@example.com";
        String password = "password";

        doReturn(true)
                .when(this.userRepository).existsByUsername(username);

        // when
        assertThrows(AlreadyExistsException.class, () ->
                this.service.createUser(username, email, password));

        // then
        verify(this.userRepository).existsByUsername(username);
        verifyNoInteractions(this.passwordEncoder);
        verifyNoMoreInteractions(this.userRepository);
    }

    @Test
    void createUser_WithExistingEmail_ThrowsAlreadyExistsException() {
        // given
        String username = "newuser";
        String email = "existing@example.com";
        String password = "password";

        doReturn(false)
                .when(this.userRepository).existsByUsername(username);
        doReturn(true)
                .when(this.userRepository).existsByEmail(email);

        // when
        assertThrows(AlreadyExistsException.class, () ->
                this.service.createUser(username, email, password));

        // then
        verify(this.userRepository).existsByUsername(username);
        verify(this.userRepository).existsByEmail(email);
        verifyNoInteractions(this.passwordEncoder);
        verifyNoMoreInteractions(this.userRepository);
    }

    @Test
    void getByUsername_UserExists_ReturnsUser() {
        // given
        String username = "username";
        User user = User.builder()
                .username(username)
                .email("username@example.com")
                .password("encoded")
                .role(Role.ROLE_USER)
                .build();

        doReturn(Optional.of(user))
                .when(this.userRepository).findByUsername(username);

        // when
        User result = this.service.getByUsername(username);

        // then
        assertEquals(user, result);

        verify(this.userRepository).findByUsername(username);
        verifyNoMoreInteractions(this.userRepository);
    }

    @Test
    void getByUsername_UserNotExists_ThrowsUsernameNotFoundException() {
        // given
        String username = "username";
        doReturn(Optional.empty())
                .when(this.userRepository).findByUsername(username);

        // when
        assertThrows(UsernameNotFoundException.class, () ->
                this.service.getByUsername(username));

        // then
        verify(this.userRepository).findByUsername(username);
        verifyNoMoreInteractions(this.userRepository);
    }
}