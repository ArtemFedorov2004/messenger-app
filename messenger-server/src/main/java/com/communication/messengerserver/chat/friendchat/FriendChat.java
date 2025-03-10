package com.communication.messengerserver.chat.friendchat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "friend_chats")
public class FriendChat {

    @Id
    private String id;

    @Indexed
    private List<String> participantIds;

    private List<String> messageIds;

    public void setParticipantIds(List<String> participantIds) {
        if (participantIds.size() != 2) {
            throw new IllegalArgumentException("Friend chat must contain exactly 2 participants");
        }

        this.participantIds = participantIds;
    }

    public void addMessageId(String messageId) {
        this.messageIds.add(messageId);
    }
}
