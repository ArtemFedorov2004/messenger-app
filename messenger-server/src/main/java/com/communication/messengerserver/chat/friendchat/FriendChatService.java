package com.communication.messengerserver.chat.friendchat;

import com.communication.messengerserver.chat.friendmessage.FriendMessage;
import com.communication.messengerserver.chat.friendmessage.FriendMessageService;
import com.communication.messengerserver.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FriendChatService {

    private final FriendChatRepository friendChatRepository;

    private final UserService userService;

    private final SimpMessagingTemplate messagingTemplate;

    private final FriendMessageService friendMessageService;

    private String getFriendId(FriendChat chat, String participantId) {
        List<String> participantIds = chat.getParticipantIds();

        return participantIds.stream()
                .filter(id -> !id.equals(participantId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Chat does not contain participant with Id: " + participantId));
    }

    public FriendChatDto toDto(FriendChat chat, String participantId) {
        String friendId = this.getFriendId(chat, participantId);

        String friendName = userService.getUsernameById(friendId);

        List<FriendMessage> messages = friendMessageService.getMessagesByIds(chat.getMessageIds());

        return FriendChatDto.builder()
                .id(chat.getId())
                .friendId(friendId)
                .friendName(friendName)
                .messages(messages)
                .build();
    }

    public List<FriendChat> getChatsForParticipant(String participantId) {
        return friendChatRepository.findChatsByParticipantId(participantId);
    }

    private void sendCreatedFriendChatNotification(FriendChat createdChat, String senderId) {
        String recipient = this.getFriendId(createdChat, senderId);
        FriendChatDto dto = this.toDto(createdChat, recipient);

        messagingTemplate.convertAndSendToUser(
                recipient,
                "/queue/chats.newFriendChat",
                dto
        );
    }

    private FriendChat createChat(List<String> participantIds, FriendMessage message) {
        Optional<FriendChat> existingChat = friendChatRepository.findByParticipantIds(participantIds);

        if (existingChat.isPresent()) {
            return existingChat.get();
        }

        FriendMessage savedMessage = friendMessageService.saveMessage(message);

        FriendChat chat = new FriendChat();
        chat.setParticipantIds(participantIds);
        chat.setMessageIds(List.of(savedMessage.getId()));

        FriendChat savedChat = friendChatRepository.save(chat);

        return savedChat;
    }

    public FriendChat createChatAndSendNotification(
            List<String> participantIds,
            FriendMessage message,
            String userId
    ) {
        FriendChat created = this.createChat(participantIds, message);
        this.sendCreatedFriendChatNotification(created, userId);

        return created;
    }

    public FriendMessage saveAndSendMessage(FriendMessage message, String recipientId) {
        FriendMessage savedMessage = friendMessageService.saveMessage(message);
        friendChatRepository.addMessageIdToChat(savedMessage.getId(),
                List.of(savedMessage.getSenderId(), recipientId));

        messagingTemplate.convertAndSendToUser(
                recipientId,
                "/queue/messages.newFriendMessage",
                savedMessage
        );

        return savedMessage;
    }
}
