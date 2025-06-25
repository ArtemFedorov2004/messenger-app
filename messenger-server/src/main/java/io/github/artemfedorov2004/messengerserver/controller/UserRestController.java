package io.github.artemfedorov2004.messengerserver.controller;

import io.github.artemfedorov2004.messengerserver.controller.payload.UserPayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.mapper.UserMapper;
import io.github.artemfedorov2004.messengerserver.entity.User;
import io.github.artemfedorov2004.messengerserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/{username}")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

    private final UserMapper userMapper;

    @GetMapping
    public UserPayload getUser(@PathVariable("username") String username) {
        User user = this.userService.getByUsername(username);

        return this.userMapper.toPayload(user);
    }
}
