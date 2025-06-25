package io.github.artemfedorov2004.messengerserver.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Message {

    private Integer id;

    private String chatId;

    private String senderName;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime editedAt;
}
