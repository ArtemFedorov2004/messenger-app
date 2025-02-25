package com.communication.messengerserver.user;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class User {

    @Id
    private String id;

    private String username;

    private String firstname;

    private String lastname;

    private String email;

    private String address;

    private String city;

    private String country;

    private int postalCode;

    private String aboutMe;
}