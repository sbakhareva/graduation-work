package ru.skypro.homework.service;

import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.exception.NoAdsFoundException;
import ru.skypro.homework.exception.NoCommentsException;
import ru.skypro.homework.exception.NoUsersFoundException;
import ru.skypro.homework.exception.NoneOfYourBusinessException;
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

@Service
public class CommentsService {

    private final CommentDTOMapper commentDTOMapper = new CommentDTOMapper();
    private final CommentsDTOMapper commentsDTOMapper = new CommentsDTOMapper(commentDTOMapper);
    private final CreateOrUpdateCommentDTOMapper createOrUpdateCommentDTOMapper = new CreateOrUpdateCommentDTOMapper(commentDTOMapper);

    private final AdRepository adRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentsService(AdRepository adRepository,
                           CommentRepository commentRepository,
                           UserRepository userRepository) {
        this.adRepository = adRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    public Comments getComments(Integer id) {
        if (!adRepository.existsById(id)) {
            throw new NoAdsFoundException("По id " + id + " не найдено объявлений.");
        }
        List<CommentEntity> comments = commentRepository.findAllByAdId(id);
        if (comments.isEmpty()) {
            throw new NoCommentsException("К этому объявлению пока нет комментариев.");
        }
        return commentsDTOMapper.toDto(comments);
    }

    public Comment addComment(Integer id,
                              CreateOrUpdateComment createOrUpdateComment,
                              String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoUsersFoundException("Не найдено пользователя с именем пользователя " + email));
        AdEntity ad = adRepository.findById(id)
                .orElseThrow(() -> new NoAdsFoundException("Не найдено объявлений с id " + id));
        return createOrUpdateCommentDTOMapper.createEntityFromDto(createOrUpdateComment, user, ad);
    }

    public void deleteComment(Integer adId,
                              Integer commentId) {
        AdEntity ad = adRepository.findById(adId)
                .orElseThrow(() -> new NoAdsFoundException("Не найдено объявлений с id " + adId));
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoCommentsException("У этого объявления пока нет комментариев."));
        if (!comment.getAd().getId().equals(adId)) {
            throw new IllegalArgumentException("Комментарий не принадлежит объявлению с id " + adId);
        }
        commentRepository.delete(comment);
    }

    public Comment updateComment(Integer adId,
                                 Integer commentId,
                                 CreateOrUpdateComment updateComment,
                                 String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoUsersFoundException("Пользователей с именем пользователя " + email + " не найдено."));
        AdEntity ad = adRepository.findById(adId)
                .orElseThrow(() -> new NoAdsFoundException("Не найдено объявлений с id " + adId));
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoCommentsException("У этого объявления пока нет комментариев."));
        if (!comment.getAuthorId().equals(user.getId())) {
            throw new NoneOfYourBusinessException("Пользователь не является автором комментария.");
        }
        if (!commentRepository.existsById(commentId)) {
            createOrUpdateCommentDTOMapper.createEntityFromDto(updateComment, user, ad);
        }
        if (!comment.getAd().getId().equals(adId)) {
            throw new IllegalArgumentException("Комментарий не принадлежит объявлению с id " + adId);
        }
        createOrUpdateCommentDTOMapper.updateEntityFromDto(updateComment, comment);
        return commentDTOMapper.toDto(comment);
    }
}
