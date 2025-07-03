package io.github.artemfedorov2004.messengerserver.service;

import io.github.artemfedorov2004.messengerserver.entity.Chat;
import io.github.artemfedorov2004.messengerserver.entity.User;
import io.github.artemfedorov2004.messengerserver.exception.AlreadyExistsException;
import io.github.artemfedorov2004.messengerserver.exception.InvalidChatParticipantsException;
import io.github.artemfedorov2004.messengerserver.exception.ResourceNotFoundException;
import io.github.artemfedorov2004.messengerserver.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DefaultChatService implements ChatService {

    private final ChatRepository chatRepository;

    private final UserService userService;

    @Override
    @Transactional
    public Chat createPrivateChat(String participantName1, String participantName2) {
        if (participantName1.equals(participantName2)) {
            throw new InvalidChatParticipantsException("messenger_server.chats.create.errors.cannot_create_with_yourself");
        }

        if (!this.userService.existsAllByUsernames(Set.of(participantName1, participantName2))) {
            throw new InvalidChatParticipantsException("messenger_server.chats.create.errors.participants_not_found");
        }

        Optional<Chat> optionalChat = this.chatRepository.findPrivateChatByParticipants(participantName1, participantName2);
        if (optionalChat.isPresent()) {
            throw new AlreadyExistsException("messenger_server.chats.create.errors.private_chat_already_exists");
        }

        Set<User> participants = Set.of(
                User.builder().username(participantName1).build(),
                User.builder().username(participantName2).build()
        );
        Chat chat = new Chat(null, participants);
        return this.chatRepository.save(chat);
    }

    @Override
    public Chat getChat(Long chatId) {
        return this.chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("messenger_server.errors.chat_not_found"));
    }

    public boolean isParticipant(Long chatId, User user) {
        return this.chatRepository.existsByIdAndParticipantsUsername(chatId, user.getUsername());
    }

    @Override
    public Iterable<Chat> getPrivateChats(User user) {
        return this.chatRepository.findAllPrivateChatsByUser(user.getUsername());
    }

    @Override
    public Set<User> getOtherParticipants(Long chatId, User user) {
        if (!this.isParticipant(chatId, user)) {
            throw new RuntimeException("User %s is not a participant of chat %d".formatted(user.getUsername(), chatId));
        }

        return this.chatRepository.findOtherParticipants(chatId, user.getUsername());
    }

    @Override
    public User getOtherParticipantInPrivateChat(Long chatId, User user) {
        if (!this.existsById(chatId)) {
            throw new ResourceNotFoundException("messenger_server.errors.chat_not_found");
        }

        return this.chatRepository.findOtherParticipantInPrivateChat(chatId, user.getUsername())
                .orElseThrow(() -> new RuntimeException("User %s is not a participant of chat %d".formatted(user.getUsername(), chatId)));
    }

    @Override
    public boolean existsById(Long chatId) {
        return this.chatRepository.existsById(chatId);
    }
}
