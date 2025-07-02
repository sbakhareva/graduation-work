package ru.skypro.homework.mappers;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.model.Comment;

/**
 * Этот маппер позволяет обновить поля сущности Comment из CreateOrUpdateComment,
 * чтобы потом с помощью CommentDTOMapper преобразовать обновленный комментарий в CommentDTO
 */
@Component
public class CreateOrUpdateCommentDTOMapper {

    public void updateEntityFromDto(CreateOrUpdateComment updateComment, Comment comment) {
        if (updateComment.getText() != null) {
            comment.setText(updateComment.getText());
        }
    }
}
