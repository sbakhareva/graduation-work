package ru.skypro.homework.service.impl;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.exception.*;
import ru.skypro.homework.mappers.CommentDTOMapper;
import ru.skypro.homework.mappers.CommentsDTOMapper;
import ru.skypro.homework.mappers.CreateOrUpdateCommentDTOMapper;
import ru.skypro.homework.model.AdEntity;
import ru.skypro.homework.model.CommentEntity;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с сущностью {@link CommentEntity}.
 * Содержит методы для создания, удаления, редактирования и получения комментариев.
 */

@Service
@Transactional
@AllArgsConstructor
public class CommentsService {

    private static final Logger logger = LoggerFactory.getLogger(CommentsService.class);

    private final CommentDTOMapper commentDTOMapper;
    private final CommentsDTOMapper commentsDTOMapper;
    private final CreateOrUpdateCommentDTOMapper createOrUpdateCommentDTOMapper;

    private final AdRepository adRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    private boolean isAdmin(String email) {
        return userRepository.findByEmail(email)
                .map(user -> user.getRole() == Role.ADMIN)
                .orElse(false);
    }

    private boolean isOwner(Integer commentId, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoUsersFoundByEmailException(email));

        return commentRepository.findById(commentId)
                .map(comment -> comment.getAuthorId().equals(user.getId()))
                .orElse(false);
    }

    /**
     * Получает все комментарии объявления.
     *
     * @param id идентификатор объявления
     * @return объект ДТО {@link Comments}
     * @throws NoAdsFoundException, если не найдено объявлений по переданному id
     */
    public Comments getComments(Integer id) {
        if (!adRepository.existsById(id)) {
            throw new NoAdsFoundException(id);
        }

        List<CommentEntity> comments = commentRepository.findAllByAdId(id);
        if (comments.isEmpty()) {
            throw new NoCommentsException();
        }

        return commentsDTOMapper.toDto(comments);
    }

    /**
     * Сохраняет новый комментарий в базу данных.
     *
     * @param id                    идентификатор объявления
     * @param createOrUpdateComment ДТО {@link CreateOrUpdateComment}, содержащий текст объявления
     * @param email                 email пользователя, извлеченный из {@link Authentication}
     * @return ДТО {@link Comment}
     * @throws NoUsersFoundByEmailException, если пользователь с указанным email не найден
     * @throws NoAdsFoundException,          если не найдено объявлений по переданному id
     */
    public Comment addComment(Integer id,
                              CreateOrUpdateComment createOrUpdateComment,
                              String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoUsersFoundByEmailException(email));

        AdEntity ad = adRepository.findById(id)
                .orElseThrow(() -> new NoAdsFoundException(id));

        CommentEntity comment = createOrUpdateCommentDTOMapper.createEntityFromDto(createOrUpdateComment, user, ad);

        commentRepository.save(comment);
        return commentDTOMapper.toDto(comment);
    }

    /**
     * Удаляет комментарий.
     *
     * @param adId      идентификатор объявления
     * @param commentId идентификатор комментария
     * @param email     email пользователя, извлеченный из {@link Authentication}
     * @throws NoCommentsException,         если у объявления пока нет комментариев
     * @throws NoneOfYourBusinessException, если у пользователя нет прав на удаление комментария
     */
    public void deleteComment(Integer adId,
                              Integer commentId,
                              String email) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(NoCommentsException::new);

        if (!isOwner(commentId, email) && !isAdmin(email)) {
            throw new NoneOfYourBusinessException("Вы не можете удалить этот комментарий");
        }

        commentRepository.delete(comment);
    }

    /**
     * Обновляет информацию о комментарии.
     *
     * @param adId          идентификатор объявления
     * @param commentId     идентификатор комментария
     * @param updateComment ДТО {@link CreateOrUpdateComment},
     *                      содержащий информацию для обновления сущности {@link CommentEntity}
     * @param email         email пользователя, извлеченный из {@link Authentication}
     * @return ДТО {@link Comment}, содержащий обновленную информацию
     * @throws NoCommentsException,         если у объявления пока нет комментариев
     * @throws NoneOfYourBusinessException, если у пользователя нет прав на редактирование комментария
     */
    public Comment updateComment(Integer adId,
                                 Integer commentId,
                                 CreateOrUpdateComment updateComment,
                                 String email) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(NoCommentsException::new);

        if (!isOwner(commentId, email) && !isAdmin(email)) {
            throw new NoneOfYourBusinessException("Вы не можете отредактировать этот комментарий");
        }

        createOrUpdateCommentDTOMapper.updateEntityFromDto(updateComment, comment);
        return commentDTOMapper.toDto(comment);
    }
}
