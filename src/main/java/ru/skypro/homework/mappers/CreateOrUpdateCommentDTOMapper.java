package ru.skypro.homework.mappers;

import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.model.CommentEntity;
import ru.skypro.homework.model.UserEntity;

import static ru.skypro.homework.utils.ImageURLGenerator.generateUserImageUrl;

/**
 * Этот маппер позволяет обновить поля сущности CommentEntity из CreateOrUpdateComment,
 * чтобы потом с помощью CommentDTOMapper преобразовать обновленный комментарий в Comment
 */
@Component
@Transactional
public class CreateOrUpdateCommentDTOMapper {

    public CommentEntity updateEntityFromDto(CreateOrUpdateComment updateComment,
                                             CommentEntity comment) {
        if (updateComment.getText() != null) {
            comment.setText(updateComment.getText());
        }
        return comment;
    }

    public CommentEntity createEntityFromDto(CreateOrUpdateComment createOrUpdateComment,
                                             UserEntity user,
                                             AdEntity ad) {
        return CommentEntity.builder()
                .authorId(user.getId())
                .authorFirstName(user.getFirstName())
                .authorImage(generateUserImageUrl(user.getId(), hasImage(user)))
                .text(createOrUpdateComment.getText())
                .ad(ad)
                .build();
    }

    private boolean hasImage(UserEntity user) {
        return Hibernate.isInitialized(user.getImage()) && user.getImage() != null;
    }
}
