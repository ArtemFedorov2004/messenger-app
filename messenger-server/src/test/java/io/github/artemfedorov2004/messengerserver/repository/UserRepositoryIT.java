package io.github.artemfedorov2004.messengerserver.repository;

import io.github.artemfedorov2004.messengerserver.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static io.github.artemfedorov2004.messengerserver.entity.Role.ROLE_USER;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql("/sql/users.sql")
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryIT {

    @Autowired
    UserRepository userRepository;

    @Test
    void findByUsername_UserExists_ReturnsUser() {
        // given
        String username = "Artem";

        // when
        Optional<User> result = this.userRepository.findByUsername(username);

        // then
        assertTrue(result.isPresent());
        assertEquals(new User("Artem", "artem@workmail.com",
                "$2a$10$gX1CW8m2TqS/ckSkoUC12ueKPfWBwYC9HtAg9prF4bxeAaZoO46me", ROLE_USER), result.get());
    }

    @Test
    void findByUsername_UserNotExists_ReturnsEmpty() {
        // given
        String username = "Margarita";

        // when
        Optional<User> result = this.userRepository.findByUsername(username);

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    void existsByUsername_UserExists_ReturnsTrue() {
        // given
        String username = "Artem";

        // when
        boolean exists = this.userRepository.existsByUsername(username);

        // then
        assertTrue(exists);
    }

    @Test
    void existsByUsername_UserNotExists_ReturnsFalse() {
        // given
        String username = "Margarita";

        // when
        boolean exists = this.userRepository.existsByUsername(username);

        // then
        assertFalse(exists);
    }

    @Test
    void existsByEmail_EmailExists_ReturnsTrue() {
        // given
        String email = "artem@workmail.com";

        // when
        boolean exists = this.userRepository.existsByEmail(email);

        // then
        assertTrue(exists);
    }

    @Test
    void existsByEmail_EmailNotExists_ReturnsFalse() {
        // given
        String email = "margo@workmail.com";

        // when
        boolean exists = this.userRepository.existsByEmail(email);

        // then
        assertFalse(exists);
    }

    @Test
    void findByUsernameContainingOrEmailContainingAllIgnoreCaseExcludingUsername_ReturnsUsers() {
        // given
        String searchTerm = "A";
        String excludeUsername = "Dima";
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<User> result = this.userRepository
                .findByUsernameContainingOrEmailContainingAllIgnoreCaseExcludingUsername(
                        searchTerm, searchTerm, excludeUsername, pageable);

        // then
        assertEquals(2, result.getTotalElements());
        assertEquals(
                List.of(new User("Artem", "artem@workmail.com",
                                "$2a$10$gX1CW8m2TqS/ckSkoUC12ueKPfWBwYC9HtAg9prF4bxeAaZoO46me", ROLE_USER),
                        new User("Pavel", "pavel@workmail.com",
                                "$2a$10$gX1CW8m2TqS/ckSkoUC12ueKPfWBwYC9HtAg9prF4bxeAaZoO46me", ROLE_USER)),
                result.getContent());
    }

    @Test
    void findByUsernameContainingOrEmailContainingAllIgnoreCaseExcludingUsername_FindsByEmail_ReturnsUsers() {
        // given
        String searchTerm = "artem@workmail.com";
        String excludeUsername = "Dima";
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<User> result = this.userRepository
                .findByUsernameContainingOrEmailContainingAllIgnoreCaseExcludingUsername(
                        searchTerm, searchTerm, excludeUsername, pageable);

        // then
        assertEquals(1, result.getTotalElements());
        assertEquals(new User("Artem", "artem@workmail.com",
                "$2a$10$gX1CW8m2TqS/ckSkoUC12ueKPfWBwYC9HtAg9prF4bxeAaZoO46me", ROLE_USER), result.getContent().getFirst());
    }

    @Test
    void findByUsernameContainingOrEmailContainingAllIgnoreCaseExcludingUsername_NoMatches_ReturnsEmpty() {
        // given
        String searchTerm = "non_existent";
        String excludeUsername = "Dima";
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<User> result = this.userRepository
                .findByUsernameContainingOrEmailContainingAllIgnoreCaseExcludingUsername(
                        searchTerm, searchTerm, excludeUsername, pageable);

        // then
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void findByUsernameContainingOrEmailContainingAllIgnoreCaseExcludingUsername_PaginationWorks() {
        // given
        String searchTerm = "@workmail.com";
        String excludeUsername = "Dima";
        Pageable pageable = PageRequest.of(0, 1);

        // when
        Page<User> result = this.userRepository
                .findByUsernameContainingOrEmailContainingAllIgnoreCaseExcludingUsername(
                        searchTerm, searchTerm, excludeUsername, pageable);

        // then
        assertEquals(1, result.getContent().size());
        assertEquals(0, result.getNumber());
        assertTrue(result.getTotalElements() > 1);
    }

    @Test
    void countAllByUsernames_AllUsersExist_ReturnsCount() {
        // given
        Collection<String> usernames = List.of("Artem", "Dima", "Pavel");

        // when
        int count = this.userRepository.countAllByUsernames(usernames);

        // then
        assertEquals(3, count);
    }

    @Test
    void countAllByUsernames_SomeUsersExist_ReturnsPartialCount() {
        // given
        Collection<String> usernames = List.of("Artem", "Dima", "NonExistentUser");

        // when
        int count = this.userRepository.countAllByUsernames(usernames);

        // then
        assertEquals(2, count);
    }

    @Test
    void countAllByUsernames_NoUsersExist_ReturnsZero() {
        // given
        Collection<String> usernames = List.of("Unknown1", "Unknown2");

        // when
        int count = this.userRepository.countAllByUsernames(usernames);

        // then
        assertEquals(0, count);
    }
}