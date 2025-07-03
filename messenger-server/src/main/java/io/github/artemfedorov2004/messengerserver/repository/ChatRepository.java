package io.github.artemfedorov2004.messengerserver.repository;

import io.github.artemfedorov2004.messengerserver.entity.Chat;
import io.github.artemfedorov2004.messengerserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    boolean existsByIdAndParticipantsUsername(Long chatId, String username);

    @Query("""
            select c from Chat c
            join c.participants p
            where p.username = :username
            and size(c.participants) = 2
            """)
    Iterable<Chat> findAllPrivateChatsByUser(String username);

    @Query("select u from Chat c join c.participants u where c.id = :chatId and u.username != :username")
    Set<User> findOtherParticipants(Long chatId, String username);

    @Query("""
                select c from Chat c
                where size(c.participants) = 2
                and exists (select 1 from c.participants p where p.username = :participant1)
                and exists (select 1 from c.participants p where p.username = :participant2)
            """)
    Optional<Chat> findPrivateChatByParticipants(String participant1, String participant2);

    @Query("""
                select u from Chat c join c.participants u
                where c.id = :chatId
                and u.username != :username
                and exists (select 1 from c.participants p where p.username = :username)
                and size(c.participants) = 2
            """)
    Optional<User> findOtherParticipantInPrivateChat(Long chatId, String username);
}
