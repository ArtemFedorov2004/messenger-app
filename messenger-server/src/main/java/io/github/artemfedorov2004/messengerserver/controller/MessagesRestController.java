package io.github.artemfedorov2004.messengerserver.controller;

import io.github.artemfedorov2004.messengerserver.controller.payload.MessagePayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.NewMessagePayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.mapper.MessageMapper;
import io.github.artemfedorov2004.messengerserver.entity.Chat;
import io.github.artemfedorov2004.messengerserver.entity.Message;
import io.github.artemfedorov2004.messengerserver.entity.User;
import io.github.artemfedorov2004.messengerserver.service.ChatService;
import io.github.artemfedorov2004.messengerserver.service.MessageService;
import io.github.artemfedorov2004.messengerserver.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

import static io.github.artemfedorov2004.messengerserver.entity.MessageNotification.Type.NEW_MESSAGE;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/private-chats/{chatId:\\d+}/messages")
public class MessagesRestController {

    private final MessageService messageService;

    private final ChatService chatService;

    private final MessageMapper messageMapper;

    private final NotificationService notificationService;

    @ModelAttribute("chat")
    public Chat getChat(@PathVariable("chatId") Long chatId) {
        return this.chatService.getChat(chatId);
    }

    @GetMapping
    @PreAuthorize("@defaultChatService.isParticipant(#chatId, #principal)")
    public Page<MessagePayload> getMessages(
            @PathVariable Long chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal User principal) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        return this.messageService.getByChatId(chatId, pageable)
                .map(this.messageMapper::toPayload);
    }

    @PostMapping
    @PreAuthorize("@defaultChatService.isParticipant(#chatId, #sender)")
    public ResponseEntity<?> createMessage(
            @PathVariable Long chatId,
            @RequestBody @Valid NewMessagePayload payload,
            @ModelAttribute("chat") Chat chat,
            @AuthenticationPrincipal User sender,
            BindingResult bindingResult,
            UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            Message createdMessage = this.messageService.createMessage(chat, sender, payload);
            MessagePayload response = this.messageMapper.toPayload(createdMessage);

            this.notificationService.send(sender, createdMessage, NEW_MESSAGE);

            return ResponseEntity
                    .created(uriComponentsBuilder
                            .replacePath("/api/chats/{chatId}/messages/{messageId}")
                            .build(Map.of("chatId", chatId, "messageId", createdMessage.getId())))
                    .body(response);
        }
    }
}
