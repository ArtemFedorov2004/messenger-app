package com.communication.messengerserver.controller;

import com.communication.messengerserver.controller.payload.EditUserPayload;
import com.communication.messengerserver.controller.payload.NewUserPayload;
import com.communication.messengerserver.entity.User;
import com.communication.messengerserver.repository.UserRepository;
import com.communication.messengerserver.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("messenger-api/users")
public class UsersRestController {

    private final UserService userService;

    @GetMapping("{userId}")
    public User findUser(@PathVariable String userId) {
        return this.userService.findUser(userId);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody NewUserPayload payload,
                                        BindingResult bindingResult,
                                        UriComponentsBuilder uriComponentsBuilder)
            throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            User user = this.userService.createUser(payload.firstname(), payload.lastname(),
                    payload.username(), payload.email());

            return ResponseEntity
                    .created(uriComponentsBuilder
                            .replacePath("messenger-api/user/{userId}")
                            .build(Map.of("userId", user.getId())))
                    .body(user);
        }
    }

    @PatchMapping("{userId}")
    public ResponseEntity<?> editUser(@Valid @RequestBody EditUserPayload payload,
                                      @PathVariable("userId") String userId,
                                      BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            this.userService.editUser(userId, payload);

            return ResponseEntity.noContent()
                    .build();
        }
    }
}
