package ru.skypro.homework.mappers;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.CommentDTO;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.Comment;

/**
 * Маппер Comment <-> CommentDTO
 */
@Component
public class CommentDTOMapper {

    public CommentDTO toDto(Comment comment) {
        return new CommentDTO(
                comment.getAuthorId(),
                comment.getAuthorImage(),
                comment.getAuthorName(),
                comment.getCreatedAt(),
                comment.getCommentId(),
                comment.getText()
        );
    }

    public Comment fromDto(CommentDTO commentDTO, Ad ad) {
        return Comment.builder()
                .authorId(commentDTO.getAuthor())
                .authorImage(commentDTO.getAuthorImage())
                .authorName(commentDTO.getAuthorName())
                .createdAt(commentDTO.getCreatedAt())
                .text(commentDTO.getText())
                .ad(ad)
                .build();
    }
}
