package ru.skypro.homework.mappers;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.model.CommentEntity;

/**
 * Этот маппер позволяет обновить поля сущности CommentEntity из CreateOrUpdateComment,
 * чтобы потом с помощью CommentDTOMapper преобразовать обновленный комментарий в Comment
 */
@Component
public class CreateOrUpdateCommentDTOMapper {

    public void updateEntityFromDto(CreateOrUpdateComment updateComment, CommentEntity commentEntity) {
        if (updateComment.getText() != null) {
            commentEntity.setText(updateComment.getText());
        }
    }
}
