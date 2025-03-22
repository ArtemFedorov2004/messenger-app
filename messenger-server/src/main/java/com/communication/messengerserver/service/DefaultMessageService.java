package com.communication.messengerserver.service;

import com.communication.messengerserver.entity.Message;
import com.communication.messengerserver.entity.User;
import com.communication.messengerserver.repository.GroupChatRepository;
import com.communication.messengerserver.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class DefaultMessageService implements MessageService {

    private final MessageRepository messageRepository;

    private final GroupChatRepository groupChatRepository;

    @Override
    public Message createMessage(String content, String senderId, String chatId) {
        User sender = User.builder()
                .id(senderId)
                .build();
        Message savedMessage = this.messageRepository.save(new Message(null, sender, content, LocalDateTime.now()));

        this.groupChatRepository.findAndPushMessageById(savedMessage.getId(), chatId);

        return savedMessage;
    }

    @Override
    public void editMessage(String messageId, String content) {
        this.messageRepository.findById(messageId)
                .ifPresentOrElse(message -> {
                    message.setContent(content);
                    this.messageRepository.save(message);
                }, () -> {
                    throw new NoSuchElementException("messenger-server.errors.message.not_found");
                });
    }

    @Override
    public void deleteMessage(String messageId) {
        this.groupChatRepository.findAndDeleteMessageById(messageId);

        this.messageRepository.deleteById(messageId);
    }
}
