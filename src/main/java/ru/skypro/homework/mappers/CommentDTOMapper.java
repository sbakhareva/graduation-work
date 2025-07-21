package ru.skypro.homework.mappers;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.model.CommentEntity;

/**
 * Маппер CommentEntity <-> Comment
 */
@Component
@Transactional
public class CommentDTOMapper {

    public Comment toDto(CommentEntity comment) {
        return new Comment(
                comment.getAuthorId(),
                comment.getAuthorImage(),
                comment.getAuthorFirstName(),
                comment.getCreatedAt(),
                comment.getId(),
                comment.getText()
        );
    }
}
