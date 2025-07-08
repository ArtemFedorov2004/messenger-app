package io.github.artemfedorov2004.messengerserver.controller;

import io.github.artemfedorov2004.messengerserver.controller.payload.UserPayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.mapper.UserMapper;
import io.github.artemfedorov2004.messengerserver.entity.Role;
import io.github.artemfedorov2004.messengerserver.entity.User;
import io.github.artemfedorov2004.messengerserver.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRestControllerTest {

    @Mock
    UserService userService;

    @Mock
    UserMapper userMapper;

    @InjectMocks
    UserRestController controller;

    @Test
    void getUser_UserExists_ReturnsUserPayload() {
        // given
        String username = "testuser";
        User user = User.builder()
                .username(username)
                .email("test@example.com")
                .password("hash")
                .role(Role.ROLE_USER)
                .build();

        doReturn(user)
                .when(this.userService).getByUsername(username);
        doReturn(new UserPayload(username, "test@example.com"))
                .when(this.userMapper).toPayload(user);

        // when
        UserPayload result = this.controller.getUser(username);

        // then
        assertEquals(new UserPayload(username, "test@example.com"), result);

        verify(this.userService).getByUsername(username);
        verifyNoMoreInteractions(this.userService);

        verify(this.userMapper).toPayload(user);
        verifyNoMoreInteractions(this.userMapper);
    }

    @Test
    void getUser_UserNotExists_ThrowsUsernameNotFoundException() {
        // given
        String username = "nonexistent";

        doThrow(new UsernameNotFoundException("User not found"))
                .when(this.userService).getByUsername(username);

        // when
        assertThrows(UsernameNotFoundException.class, () ->
                this.controller.getUser(username));

        // then
        verify(this.userService).getByUsername(username);
        verifyNoMoreInteractions(this.userService);
        verifyNoInteractions(this.userMapper);
    }
}