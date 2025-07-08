package io.github.artemfedorov2004.messengerserver.controller.payload.mapper;

import io.github.artemfedorov2004.messengerserver.controller.payload.UserPayload;
import io.github.artemfedorov2004.messengerserver.entity.Role;
import io.github.artemfedorov2004.messengerserver.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @InjectMocks
    UserMapper userMapper;

    @Test
    void toPayload_SingleUser_ReturnsPayload() {
        // given
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("hash")
                .role(Role.ROLE_USER)
                .build();

        // when
        UserPayload result = this.userMapper.toPayload(user);

        // then
        assertEquals(new UserPayload("testuser", "test@example.com"), result);
    }

    @Test
    void fromPayload_ReturnsUser() {
        // given
        UserPayload payload = new UserPayload("newuser", "new@example.com");

        // when
        User result = userMapper.fromPayload(payload);

        // then
        assertEquals(User.builder()
                .username("newuser")
                .email("new@example.com")
                .build(), result);
    }

    @Test
    void toPayload_UserList_ReturnsPayloadList() {
        // given
        List<User> users = List.of(
                User.builder().username("user1").email("user1@example.com").build(),
                User.builder().username("user2").email("user2@example.com").build()
        );

        // when
        Iterable<UserPayload> result = this.userMapper.toPayload(users);

        // then
        assertEquals(List.of(new UserPayload("user1", "user1@example.com"),
                new UserPayload("user2", "user2@example.com")), result);
    }

    @Test
    void toPayload_EmptyList_ReturnsEmptyList() {
        // given
        List<User> emptyList = Collections.emptyList();

        // when
        Iterable<UserPayload> result = userMapper.toPayload(emptyList);

        // then
        assertNotNull(result);
        assertFalse(result.iterator().hasNext());
    }
}