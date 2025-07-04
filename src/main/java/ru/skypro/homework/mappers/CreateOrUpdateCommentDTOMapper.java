package ru.skypro.homework.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.model.CommentEntity;

/**
 * Этот маппер позволяет обновить поля сущности CommentEntity из CreateOrUpdateComment,
 * чтобы потом с помощью CommentDTOMapper преобразовать обновленный комментарий в Comment
 */
@Component
public class CreateOrUpdateCommentDTOMapper {

    private final CommentDTOMapper commentDTOMapper = new CommentDTOMapper();

    public Comment updateEntityFromDto(CreateOrUpdateComment updateComment, CommentEntity comment) {
        if (updateComment.getText() != null) {
            comment.setText(updateComment.getText());
        }
        return commentDTOMapper.toDto(comment);
    }
}
