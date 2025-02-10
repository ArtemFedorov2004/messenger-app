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

@Repository
@RequiredArgsConstructor
public class MessageRepository implements RowMapper<Message> {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
        Integer id = rs.getInt("id");
        String content = rs.getString("content");
        LocalDateTime createdAt = rs.getObject("created_at", LocalDateTime.class);

        return new Message(id, content, createdAt);
    }

    public Message save(Message message) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                connection -> {
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

    public List<Message> findAll() {
        return jdbcTemplate.query("SELECT * FROM message", this);
    }
}
