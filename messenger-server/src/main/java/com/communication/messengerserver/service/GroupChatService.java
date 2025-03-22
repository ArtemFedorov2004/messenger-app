package com.communication.messengerserver.service;

import com.communication.messengerserver.entity.GroupChat;
import com.communication.messengerserver.entity.Message;
import com.communication.messengerserver.entity.User;

import java.util.List;

public interface GroupChatService {

    Iterable<GroupChat> findGroupChats();

    GroupChat findGroupChat(String chatId);

    GroupChat createGroupChat(String title, List<String> memberIds);

    void deleteGroupChat(String chatId);

    Iterable<User> findMembers(String chatId);

    Iterable<Message> findMessages(String chatId);

    void addUserToChatMembers(String userId, String chatId);

    void deleteChatMember(String memberId, String chatId);
}
