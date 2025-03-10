package com.communication.messengerserver.chat.friendchat;

import com.communication.messengerserver.chat.friendmessage.FriendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/friend-chats")
public class FriendChatController {

    private final FriendChatService friendChatService;

    @GetMapping
    public ResponseEntity<List<FriendChatDto>> getUserFriendChats(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();

        List<FriendChat> friendChats = friendChatService.getChatsForParticipant(userId);

        List<FriendChatDto> dtos = friendChats.stream()
                .map(chat -> friendChatService.toDto(chat, userId))
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<FriendChatDto> createChat(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreateFriendChatRequest request
    ) {
        String userId = jwt.getSubject();

        List<String> participantIds = List.of(userId, request.friendId());
        FriendMessage message = request.message();

        FriendChat created = friendChatService.createChatAndSendNotification(participantIds, message, userId);

        FriendChatDto dto = friendChatService.toDto(created, userId);

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{friendId}/messages")
    public ResponseEntity<FriendMessage> sendMessage(@PathVariable String friendId, @RequestBody FriendMessage message) {
        FriendMessage sentMessage = friendChatService.saveAndSendMessage(message, friendId);

        return ResponseEntity.ok(sentMessage);
    }
}
