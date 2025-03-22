package com.communication.messengerserver.service;

import com.communication.messengerserver.entity.Message;

public interface MessageService {

    Message createMessage(String content, String senderId, String chatId);

    void editMessage(String messageId, String content);

    void deleteMessage(String messageId);
}
