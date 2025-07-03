package io.github.artemfedorov2004.messengerserver.repository;

import io.github.artemfedorov2004.messengerserver.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("""
             select u from User u where u.username != ?3
             and (lower(u.username) like lower(concat('%', ?1, '%'))
             or lower(u.email) like lower(concat('%', ?2, '%')))
            """)
    Page<User> findByUsernameContainingOrEmailContainingAllIgnoreCaseExcludingUsername(
            String usernameQuery,
            String emailQuery,
            String excludeUsername,
            Pageable pageable);

    @Query("select count(u) from User u where u.username in :usernames")
    int countAllByUsernames(Collection<String> usernames);
}
