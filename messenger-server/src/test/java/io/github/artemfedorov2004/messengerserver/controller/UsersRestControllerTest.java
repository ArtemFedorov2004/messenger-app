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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersRestControllerTest {

    @Mock
    UserService userService;

    @Mock
    UserMapper userMapper;

    @InjectMocks
    UsersRestController controller;

    @Test
    void searchUsers_WithValidQuery_ReturnsMappedPage() {
        // given
        String query = "test";
        int page = 0;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);

        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("hash")
                .role(Role.ROLE_USER)
                .build();

        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);
        UserPayload payload = new UserPayload("testuser", "test@example.com");

        doReturn(userPage)
                .when(this.userService).searchUsers(query, pageable);
        doReturn(payload)
                .when(this.userMapper).toPayload(user);

        // when
        Page<UserPayload> result = this.controller.searchUsers(query, page, size);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(payload, result.getContent().get(0));

        verify(this.userService).searchUsers(query, pageable);
        verifyNoMoreInteractions(this.userService);

        verify(this.userMapper).toPayload(user);
        verifyNoMoreInteractions(this.userMapper);
    }

    @Test
    void searchUsers_WithEmptyQuery_ReturnsEmptyPage() {
        // given
        String query = "";
        int page = 0;
        int size = 5;

        // when
        Page<UserPayload> result = this.controller.searchUsers(query, page, size);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verifyNoInteractions(this.userService, this.userMapper);
    }
}