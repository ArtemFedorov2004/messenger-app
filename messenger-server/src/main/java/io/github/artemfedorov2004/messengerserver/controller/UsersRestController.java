package io.github.artemfedorov2004.messengerserver.controller;

import io.github.artemfedorov2004.messengerserver.controller.payload.UserPayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.mapper.UserMapper;
import io.github.artemfedorov2004.messengerserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersRestController {

    private final UserService userService;

    private final UserMapper userMapper;

    @GetMapping
    public Page<UserPayload> searchUsers(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        if (StringUtils.isEmpty(query)) {
            return Page.empty();
        }

        Pageable pageable = PageRequest.of(page, size);
        return this.userService.searchUsers(query, pageable)
                .map(this.userMapper::toPayload);
    }
}
