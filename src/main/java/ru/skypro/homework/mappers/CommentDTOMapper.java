package ru.skypro.homework.mappers;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.model.CommentEntity;

/**
 * Маппер CommentEntity <-> Comment
 */
@Component
public class CommentDTOMapper {

    public Comment toDto(CommentEntity commentEntity) {
        return new Comment(
                commentEntity.getAuthorId(),
                commentEntity.getAuthorImage(),
                commentEntity.getAuthorName(),
                commentEntity.getCreatedAt(),
                commentEntity.getId(),
                commentEntity.getText()
        );
    }

    public CommentEntity fromDto(Comment comment, AdEntity adEntity) {
        return CommentEntity.builder()
                .authorId(comment.getAuthor())
                .authorImage(comment.getAuthorImage())
                .authorName(comment.getAuthorName())
                .createdAt(comment.getCreatedAt())
                .text(comment.getText())
                .adEntity(adEntity)
                .build();
    }
}
