package com.communication.messengerserver.controller.payload;

public record EditUserPayload(
        String username,
        String firstname,
        String lastname,
        String email,
        String address,
        String city,
        String country,
        Integer postalCode,
        String aboutMe
) {
}
