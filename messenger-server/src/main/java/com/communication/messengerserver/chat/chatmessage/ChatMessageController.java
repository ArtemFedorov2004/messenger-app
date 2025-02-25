package com.communication.messengerserver.chat.chatmessage;

import com.communication.messengerserver.chat.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chats/{chatId}/messages")
@MessageMapping("/chats/{chatId}/messages")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    private final ChatRepository chatRepository;

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping
    public void sendMessage(@DestinationVariable String chatId, @Payload ChatMessageDto chatMessage) {
        ChatMessage savedMessage = chatMessageService.saveMessage(chatId, chatMessage.toChatMessage());
        String friendId = chatRepository.findFriendId(chatId, savedMessage.getSenderId());
        messagingTemplate.convertAndSendToUser(
                friendId,
                "/queue/messages",
                savedMessage
        );
    }
}
