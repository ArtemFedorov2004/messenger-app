package com.communication.messengerserver.chat.friendmessage;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "friend_messages")
public class FriendMessage {

    @Id
    private String id;

    private String senderId;

    private String content;

    private Date createdAt;
}
