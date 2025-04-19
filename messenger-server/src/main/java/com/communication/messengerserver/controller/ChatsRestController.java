package com.communication.messengerserver.controller;

import com.communication.messengerserver.controller.payload.ChatPayload;
import com.communication.messengerserver.controller.payload.EditMessagePayload;
import com.communication.messengerserver.controller.payload.NewMessagePayload;
import com.communication.messengerserver.entity.Chat;
import com.communication.messengerserver.entity.Message;
import com.communication.messengerserver.entity.MessageNotification;
import com.communication.messengerserver.service.ChatService;
import com.communication.messengerserver.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import static com.communication.messengerserver.entity.MessageNotification.Type.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chats")
public class ChatsRestController {

    private final ChatService chatService;

    private final NotificationService notificationService;

    @GetMapping
    List<ChatPayload> getUserChats(Principal principal) {
        String username = principal.getName();
        List<Chat> chats = this.chatService.getUserChats(username);

        return chats.stream()
                .map(chat -> chat.toPayload(username))
                .toList();
    }

    @PostMapping("{chatId}/messages")
    public ResponseEntity<?> createMessage(@Valid @RequestBody NewMessagePayload payload,
                                           @PathVariable("chatId") String chatId,
                                           BindingResult bindingResult,
                                           UriComponentsBuilder uriComponentsBuilder,
                                           Principal principal)
            throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            Message message = this.chatService.createMessage(chatId, principal.getName(), payload.content());

            this.notificationService.send(new MessageNotification(NEW_MESSAGE, message));

            return ResponseEntity
                    .created(uriComponentsBuilder
                            .replacePath("api/chats/{chatId}/messages/{messageId}")
                            .build(Map.of("chatId", chatId, "messageId", message.getId())))
                    .body(message);
        }
    }

    @PatchMapping("{chatId}/messages/{messageId:\\d+}")
    public ResponseEntity<?> editMessage(@Valid @RequestBody EditMessagePayload payload,
                                         @PathVariable("chatId") String chatId,
                                         @PathVariable("messageId") Integer messageId,
                                         BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            Message message = this.chatService.editMessage(chatId, messageId, payload.content());

            this.notificationService.send(new MessageNotification(EDIT_MESSAGE, message));

            return ResponseEntity.ok(message);
        }
    }

    @DeleteMapping("{chatId}/messages/{messageId:\\d+}")
    public ResponseEntity<Void> deleteMessage(@PathVariable("chatId") String chatId,
                                              @PathVariable("messageId") Integer messageId,
                                              Principal principal) {
        boolean isDeleted = this.chatService.deleteMessage(chatId, messageId);

        if (isDeleted) {
            this.notificationService.send(new MessageNotification(DELETE_MESSAGE,
                    Message.builder()
                            .chatId(chatId)
                            .id(messageId)
                            .senderName(principal.getName())
                            .build()
            ));
        }

        return ResponseEntity.noContent()
                .build();
    }
}
