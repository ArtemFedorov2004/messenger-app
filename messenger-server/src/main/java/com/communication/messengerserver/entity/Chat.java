package com.communication.messengerserver.entity;

import com.communication.messengerserver.controller.payload.ChatPayload;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Objects;

@Data
@Document(collection = "user_chats")
public class Chat {

    @Id
    private String id;

    @Field("member_names")
    private List<String> memberNames;

    private List<Message> messages;

    public ChatPayload toPayload(String username) {
        if (!this.memberNames.contains(username)) {
            throw new IllegalArgumentException("User with id '%s' is not a member of this chat.".formatted(username));
        }

        String friendName = this.memberNames.stream()
                .filter(name -> !Objects.equals(username, name))
                .findFirst()
                .orElseThrow();

        return new ChatPayload(this.id, friendName, this.messages);
    }
}
