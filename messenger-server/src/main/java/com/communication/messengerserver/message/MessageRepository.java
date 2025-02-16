package com.communication.messengerserver.message;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MessageRepository implements RowMapper<Message> {

    private final JdbcTemplate jdbcTemplate;

    private Message insert(Message message) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO message (content, created_at) VALUES (?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, message.getContent());
            ps.setObject(2, message.getCreatedAt());

            return ps;
        }, keyHolder);

        if (keyHolder.getKeys() != null && keyHolder.getKeys().containsKey("id")) {
            Integer id = (Integer) (keyHolder.getKeys().get("id"));
            return jdbcTemplate.queryForObject("SELECT * FROM message WHERE id = ?", this, id);
        } else {
            throw new RuntimeException("Message was not saved into the database.");
        }
    }

    private Message update(Message message) {
        jdbcTemplate.update("UPDATE message SET content = ?, created_at = ? WHERE id = ?",
                message.getContent(),
                message.getCreatedAt(),
                message.getId());

        return jdbcTemplate.queryForObject("SELECT * FROM message WHERE id = ?", this, message.getId());
    }

    @Override
    public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
        Integer id = rs.getInt("id");
        String content = rs.getString("content");
        LocalDateTime createdAt = rs.getObject("created_at", LocalDateTime.class);

        return new Message(id, content, createdAt);
    }

    public Message save(Message message) {
        if (message.getId() == null) {
            return insert(message);
        } else {
            return update(message);
        }
    }

    public List<Message> findAll() {
        return jdbcTemplate.query("SELECT * FROM message", this);
    }

    public Optional<Message> findById(Integer id) {
        return Optional.of(jdbcTemplate.queryForObject("SELECT * FROM message WHERE id = ?", this, id));
    }
}
