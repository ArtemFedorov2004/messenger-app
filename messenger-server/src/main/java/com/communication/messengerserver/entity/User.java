package com.communication.messengerserver.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("users")
public class User {

    @Id
    private String id;

    @NotNull
    @NotBlank
    private String username;

    @NotNull
    @NotBlank
    private String firstname;

    @NotNull
    @NotBlank
    private String lastname;

    @NotNull
    @NotBlank
    @Email
    private String email;

    private String address;

    private String city;

    private String country;

    @Field(name = "postal_code")
    private Integer postalCode;

    @Field("about_me")
    private String aboutMe;
}
