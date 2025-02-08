package com.communication.messengerserver.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class Message {

    private Integer id;

    private String content;

    private LocalDateTime createdAt;
}
