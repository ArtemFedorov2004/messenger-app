package com.communication.messengerserver.controller.mapper;

import com.communication.messengerserver.controller.payload.EditUserPayload;
import com.communication.messengerserver.entity.User;

public class UserMapper {

    public static User toUser(EditUserPayload payload) {
        return User.builder()
                .username(payload.username())
                .firstname(payload.firstname())
                .lastname(payload.lastname())
                .email(payload.email())
                .address(payload.address())
                .city(payload.city())
                .country(payload.country())
                .postalCode(payload.postalCode())
                .aboutMe(payload.aboutMe())
                .build();
    }
}
