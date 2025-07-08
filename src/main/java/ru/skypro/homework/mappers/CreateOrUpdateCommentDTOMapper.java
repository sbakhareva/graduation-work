package ru.skypro.homework.mappers;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.model.CommentEntity;
import ru.skypro.homework.model.UserEntity;

import static ru.skypro.homework.utils.ImageURLGenerator.generateImageUrl;

/**
 * Этот маппер позволяет обновить поля сущности CommentEntity из CreateOrUpdateComment,
 * чтобы потом с помощью CommentDTOMapper преобразовать обновленный комментарий в Comment
 */
@Component
@Transactional
public class CreateOrUpdateCommentDTOMapper {

    private final CommentDTOMapper commentDTOMapper;

    public CreateOrUpdateCommentDTOMapper(CommentDTOMapper commentDTOMapper) {
        this.commentDTOMapper = commentDTOMapper;
    }

    public Comment updateEntityFromDto(CreateOrUpdateComment updateComment, CommentEntity comment) {
        if (updateComment.getText() != null) {
            comment.setText(updateComment.getText());
        }
        return commentDTOMapper.toDto(comment);
    }

    public Comment createEntityFromDto(CreateOrUpdateComment createOrUpdateComment,
                                       UserEntity user,
                                       AdEntity ad) {
        CommentEntity commentEntity = CommentEntity.builder()
                .authorId(user.getId())
                .authorName(user.getFirstName())
                .authorImage(generateImageUrl(user))
                .text(createOrUpdateComment.getText())
                .ad(ad)
                .build();
        return commentDTOMapper.toDto(commentEntity);
    }
}
