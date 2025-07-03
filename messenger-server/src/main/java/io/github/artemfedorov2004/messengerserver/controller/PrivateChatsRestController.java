package io.github.artemfedorov2004.messengerserver.controller;

import io.github.artemfedorov2004.messengerserver.controller.payload.NewPrivateChatPayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.PrivateChatPayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.mapper.ChatMapper;
import io.github.artemfedorov2004.messengerserver.entity.Chat;
import io.github.artemfedorov2004.messengerserver.entity.User;
import io.github.artemfedorov2004.messengerserver.service.ChatService;
import io.github.artemfedorov2004.messengerserver.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

import static io.github.artemfedorov2004.messengerserver.entity.PrivateChatNotification.Type.NEW_CHAT;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/private-chats")
public class PrivateChatsRestController {

    private final ChatService chatService;

    private final ChatMapper chatMapper;

    private final NotificationService notificationService;

    @GetMapping
    public Iterable<PrivateChatPayload> getPrivateChats(
            @AuthenticationPrincipal User principal) {
        Iterable<Chat> chats = this.chatService.getPrivateChats(principal);

        return this.chatMapper.toPayload(chats, principal);
    }

    @PostMapping
    public ResponseEntity<?> createPrivateChat(@Valid @RequestBody NewPrivateChatPayload payload,
                                               @AuthenticationPrincipal User principal,
                                               BindingResult bindingResult,
                                               UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            Chat createdChat = this.chatService.createPrivateChat(principal.getUsername(), payload.participantName());
            PrivateChatPayload response = this.chatMapper.toPayload(createdChat, principal);

            this.notificationService.send(principal, createdChat, NEW_CHAT);

            return ResponseEntity
                    .created(uriComponentsBuilder
                            .replacePath("/api/chats/{chatId}")
                            .build(Map.of("chatId", createdChat.getId())))
                    .body(response);
        }
    }
}
