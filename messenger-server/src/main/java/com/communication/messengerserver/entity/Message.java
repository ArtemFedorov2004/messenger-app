package com.communication.messengerserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("messages")
public class Message {

    private String id;

    @JsonIgnore
    @DBRef
    private User sender;

    private String content;

    @Field(name = "created_at")
    private LocalDateTime createdAt;
}
