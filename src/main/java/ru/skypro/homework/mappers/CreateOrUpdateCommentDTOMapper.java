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
 * Маппер для обновления полей сущности {@link CommentEntity} из {@link CreateOrUpdateComment}
 * или создания на основе {@link CreateOrUpdateComment} новой сущности для сохранения в базу данных.
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
                .authorImage(generateUserImageUrl(user.getImage()))
                .text(createOrUpdateComment.getText())
                .ad(ad)
                .build();
    }
}
