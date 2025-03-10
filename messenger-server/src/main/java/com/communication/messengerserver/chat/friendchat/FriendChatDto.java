package com.communication.messengerserver.chat.friendchat;

import com.communication.messengerserver.chat.friendmessage.FriendMessage;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendChatDto {

    private String id;

    private String friendId;

    private String friendName;

    private List<FriendMessage> messages;
}
