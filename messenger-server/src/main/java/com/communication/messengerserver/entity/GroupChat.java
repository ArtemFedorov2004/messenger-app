package com.communication.messengerserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("group_chats")
public class GroupChat {

    @Id
    private String id;

    @NotNull
    @NotBlank
    private String title;

    @JsonIgnore
    @DBRef(lazy = true)
    @NotNull
    private List<User> members;

    @JsonIgnore
    @DBRef(lazy = true)
    private List<Message> messages;
}
