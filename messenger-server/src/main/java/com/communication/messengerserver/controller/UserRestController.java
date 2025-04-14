package com.communication.messengerserver.controller;

import com.communication.messengerserver.controller.payload.UserPayload;
import com.communication.messengerserver.entity.User;
import com.communication.messengerserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

    @GetMapping
    public UserPayload getCurrentUser() {
        User user = userService.getCurrentUser();
        return user.toPayload();
    }
}
