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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    void searchUsersExcludingCurrent_ReturnsFilteredUsers() {
        // given
        String query = "test";
        User currentUser = new User("current", "current@test.com", "pass", Role.ROLE_USER);
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = List.of(
                new User("test1", "test1@example.com", "pass", Role.ROLE_USER),
                new User("test2", "test2@example.com", "pass", Role.ROLE_USER)
        );
        Page<User> expectedPage = new PageImpl<>(users, pageable, 2);

        doReturn(expectedPage)
                .when(this.userRepository).findByUsernameContainingOrEmailContainingAllIgnoreCaseExcludingUsername(
                        query, query, "current", pageable
                );

        // when
        Page<User> result = this.service.searchUsersExcludingCurrent(query, currentUser, pageable);

        // then
        assertEquals(expectedPage, result);
        verify(this.userRepository).findByUsernameContainingOrEmailContainingAllIgnoreCaseExcludingUsername(
                query, query, currentUser.getUsername(), pageable
        );
        verifyNoMoreInteractions(this.userRepository, this.passwordEncoder);
    }

    @Test
    void searchUsersExcludingCurrent_WhenEmptyQuery_ReturnsEmptyPage() {
        // given
        String query = "";
        User currentUser = new User("current", "current@test.com", "pass", Role.ROLE_USER);
        Pageable pageable = PageRequest.of(0, 10);

        doReturn(Page.empty())
                .when(this.userRepository).findByUsernameContainingOrEmailContainingAllIgnoreCaseExcludingUsername(
                        query, query, "current", pageable
                );

        // when
        Page<User> result = this.service.searchUsersExcludingCurrent(query, currentUser, pageable);

        // then
        assertTrue(result.isEmpty());
        verify(this.userRepository).findByUsernameContainingOrEmailContainingAllIgnoreCaseExcludingUsername(
                query, query, "current", pageable
        );
        verifyNoInteractions(this.passwordEncoder);
    }

    @Test
    void existsAllByUsernames_WhenAllExist_ReturnsTrue() {
        // given
        Set<String> usernames = Set.of("user1", "user2");

        doReturn(2)
                .when(this.userRepository).countAllByUsernames(usernames);

        // when
        boolean result = this.service.existsAllByUsernames(usernames);

        // then
        assertTrue(result);
        verify(this.userRepository).countAllByUsernames(usernames);
        verifyNoMoreInteractions(this.userRepository, this.passwordEncoder);
    }

    @Test
    void existsAllByUsernames_WhenSomeMissing_ReturnsFalse() {
        // given
        Set<String> usernames = Set.of("user1", "user2", "user3");

        doReturn(2)
                .when(this.userRepository).countAllByUsernames(usernames);

        // when
        boolean result = this.service.existsAllByUsernames(usernames);

        // then
        assertFalse(result);
        verify(this.userRepository).countAllByUsernames(usernames);
        verifyNoMoreInteractions(this.userRepository, this.passwordEncoder);
    }

    @Test
    void existsAllByUsernames_UsernamesCollectionContainsDuplicatesAndAllExists_ReturnsTrue() {
        // given
        Collection<String> usernames = List.of("user1", "user1", "user3");

        doReturn(2)
                .when(this.userRepository).countAllByUsernames(usernames);

        // when
        boolean result = this.service.existsAllByUsernames(usernames);

        // then
        assertTrue(result);
        verify(this.userRepository).countAllByUsernames(usernames);
        verifyNoMoreInteractions(this.userRepository, this.passwordEncoder);
    }
}