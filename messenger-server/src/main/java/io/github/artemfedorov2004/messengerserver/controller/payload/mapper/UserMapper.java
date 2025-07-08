package io.github.artemfedorov2004.messengerserver.controller.payload.mapper;

import io.github.artemfedorov2004.messengerserver.controller.payload.UserPayload;
import io.github.artemfedorov2004.messengerserver.entity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class UserMapper implements Mappable<User, UserPayload> {

    @Override
    public User fromPayload(UserPayload payload) {
        return User.builder()
                .username(payload.username())
                .email(payload.email())
                .build();
    }

    @Override
    public UserPayload toPayload(User entity) {
        return new UserPayload(entity.getUsername(), entity.getEmail());
    }

    @Override
    public Iterable<UserPayload> toPayload(Iterable<User> entities) {
        return StreamSupport.stream(entities.spliterator(), false)
                .map(this::toPayload)
                .collect(Collectors.toList());
    }
}
