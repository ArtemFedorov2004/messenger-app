package com.communication.messengerserver.chat.friendchat;

import com.communication.messengerserver.chat.friendmessage.FriendMessage;


public record CreateFriendChatRequest(String friendId, FriendMessage message) {
}

