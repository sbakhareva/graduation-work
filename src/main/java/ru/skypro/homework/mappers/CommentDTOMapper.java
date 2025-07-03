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

    public Comment toDto(CommentEntity comment) {
        return new Comment(
                comment.getAuthorId(),
                comment.getAuthorImage(),
                comment.getAuthorName(),
                comment.getCreatedAt(),
                comment.getId(),
                comment.getText()
        );
    }

    public CommentEntity fromDto(Comment comment, AdEntity ad) {
        return CommentEntity.builder()
                .authorId(comment.getAuthor())
                .authorImage(comment.getAuthorImage())
                .authorName(comment.getAuthorName())
                .createdAt(comment.getCreatedAt())
                .text(comment.getText())
                .ad(ad)
                .build();
    }
}
