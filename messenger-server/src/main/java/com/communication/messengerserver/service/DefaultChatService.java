package com.communication.messengerserver.service;

import com.communication.messengerserver.entity.Chat;
import com.communication.messengerserver.entity.Message;
import com.communication.messengerserver.entity.User;
import com.communication.messengerserver.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DefaultChatService implements ChatService {

    private final ChatRepository chatRepository;

    // этого здесь не должно быть
    private final MongoTemplate mongoTemplate;

    public List<Chat> getUserChats(String username) {
        return this.chatRepository.findAllByMember(username);
    }

    @Override
    public List<String> getMembersExcept(String chatId, String username) {
        if (!this.chatRepository.exists(chatId)) {
            throw new NoSuchElementException("Chat with id '%s' does not exist.".formatted(chatId));
        }

        List<String> memberNames = chatRepository.getMemberNames(chatId);

        return memberNames.stream()
                .filter(name -> !Objects.equals(name, username))
                .toList();
    }

    public Message createMessage(String chatId, String senderName, String content) {
        if (!this.chatRepository.exists(chatId)) {
            throw new NoSuchElementException("messenger_server.errors.chat_not_found");
        }

        return this.chatRepository.pushMessageToChat(chatId,
                new Message(null, chatId, senderName, content, LocalDateTime.now(), null));
    }

    @Override
    public Message editMessage(String chatId, Integer messageId, String content) {
        if (!this.chatRepository.exists(chatId)) {
            throw new NoSuchElementException("messenger_server.errors.chat_not_found");
        }

        Optional<Message> optionalMessage = this.chatRepository.findMessageInChat(chatId, messageId);

        if (optionalMessage.isEmpty()) {
            throw new NoSuchElementException("messenger_server.errors.message_not_found");
        }

        Message message = optionalMessage.get();
        message.setContent(content);
        message.setEditedAt(LocalDateTime.now());

        this.chatRepository.updateMessageInChat(chatId, message);

        return message;
    }

    @Override
    public boolean deleteMessage(String chatId, Integer messageId) {
        return this.chatRepository.deleteMessage(chatId, messageId);
    }


    // этого здесь не должно быть
    @Override
    public void createChatForNewUser(User user) {
        List<User> users = mongoTemplate.findAll(User.class);
        users.remove(user);
        users.forEach(u -> {
            Chat chat = new Chat();
            chat.setMessages(List.of());
            chat.setMemberNames(List.of(u.getUsername(), user.getUsername()));
            mongoTemplate.save(chat);
        });
    }
}
