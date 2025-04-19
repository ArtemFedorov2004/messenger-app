package com.communication.messengerserver.service;

import com.communication.messengerserver.entity.Chat;
import com.communication.messengerserver.entity.Message;
import com.communication.messengerserver.entity.User;

import java.util.List;

public interface ChatService {

    List<Chat> getUserChats(String username);

    List<String> getMembersExcept(String chatId, String username);

    Message createMessage(String chatId, String senderName, String content);

    Message editMessage(String chatId, Integer messageId, String content);

    boolean deleteMessage(String chatId, Integer messageId);

    // этого здесь не должно быть
    void createChatForNewUser(User user);
}
