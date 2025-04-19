package com.communication.messengerserver.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Message {

    private Integer id;

    @Field("chat_id")
    private String chatId;

    @Field("sender_name")
    private String senderName;

    private String content;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("edited_at")
    private LocalDateTime editedAt;
}
