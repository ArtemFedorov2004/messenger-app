package com.communication.messengerserver.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class User {
    @Id
    private String id;

    private String email;

    private String firstname;

    private String lastname;

    private Status status;
}
