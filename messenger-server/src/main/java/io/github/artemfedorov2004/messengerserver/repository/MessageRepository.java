package io.github.artemfedorov2004.messengerserver.repository;

import io.github.artemfedorov2004.messengerserver.entity.Message;
import io.github.artemfedorov2004.messengerserver.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByChatId(Long chatId, Pageable pageable);

    boolean existsByIdAndSender(Long messageId, User principal);

    boolean existsByIdAndChatParticipantsContaining(Long messageId, User user);

    @Query("select m from Message m " +
            "where m.chat.id = :chatId " +
            "order by m.createdAt desc " +
            "limit 1")
    Optional<Message> findLastMessageByChatId(Long chatId);
}
