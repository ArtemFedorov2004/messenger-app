package io.github.artemfedorov2004.messengerserver.controller;

import io.github.artemfedorov2004.messengerserver.controller.payload.MessagePayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.UpdateMessagePayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.mapper.MessageMapper;
import io.github.artemfedorov2004.messengerserver.entity.Message;
import io.github.artemfedorov2004.messengerserver.entity.User;
import io.github.artemfedorov2004.messengerserver.service.MessageService;
import io.github.artemfedorov2004.messengerserver.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static io.github.artemfedorov2004.messengerserver.entity.MessageNotification.Type.DELETE_MESSAGE;
import static io.github.artemfedorov2004.messengerserver.entity.MessageNotification.Type.EDIT_MESSAGE;
import static io.github.artemfedorov2004.messengerserver.entity.PrivateChatNotification.Type.EDIT_CHAT;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/private-chat-messages/{messageId:\\d+}")
public class MessageRestController {

    private final MessageService messageService;

    private final NotificationService notificationService;

    private final MessageMapper messageMapper;

    @ModelAttribute("message")
    @PreAuthorize("@defaultMessageService.canGetMessage(#messageId, #principal)")
    public Message getMessage(@PathVariable("messageId") Long messageId,
                              @AuthenticationPrincipal User principal) {
        return this.messageService.getMessage(messageId);
    }

    @PatchMapping
    @PreAuthorize("@defaultMessageService.canEditMessage(#messageId, #principal)")
    public ResponseEntity<?> updateMessage(
            @PathVariable("messageId") Long messageId,
            @AuthenticationPrincipal User principal,
            @Valid @RequestBody UpdateMessagePayload payload,
            BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            Message updatedMessage = this.messageService.updateMessage(messageId, payload);
            MessagePayload response = this.messageMapper.toPayload(updatedMessage);

            this.notificationService.send(principal, updatedMessage, EDIT_MESSAGE);

            return ResponseEntity.ok(response);
        }
    }

    @DeleteMapping
    @PreAuthorize("@defaultMessageService.canDeleteMessage(#message.getId(), #principal)")
    public ResponseEntity<Void> deleteMessage(@ModelAttribute("message") Message message,
                                              @AuthenticationPrincipal User principal) {
        this.messageService.deleteMessage(message.getId());

        this.notificationService.send(principal, message.getChat(), EDIT_CHAT);

        this.notificationService.send(principal, message, DELETE_MESSAGE);

        return ResponseEntity.noContent()
                .build();
    }
}
