package com.communication.messengerserver.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/{userId}")
    public ResponseEntity<?> editUser(
            @PathVariable String userId,
            @RequestBody User userToEdit,
            @AuthenticationPrincipal Jwt jwt
            ) {
        String userIdFromSubject = jwt.getSubject();

        if (!userIdFromSubject.equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userService.editUser(userIdFromSubject, userToEdit);

        return ResponseEntity.noContent()
                .build();
    }
}
