package ru.skypro.homework.service;

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

@Service
@Transactional
public class CommentsService {

    private final CommentDTOMapper commentDTOMapper;
    private final CommentsDTOMapper commentsDTOMapper;
    private final CreateOrUpdateCommentDTOMapper createOrUpdateCommentDTOMapper;

    private final AdRepository adRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentsService(CommentDTOMapper commentDTOMapper,
                           CommentsDTOMapper commentsDTOMapper,
                           CreateOrUpdateCommentDTOMapper createOrUpdateCommentDTOMapper,
                           AdRepository adRepository,
                           CommentRepository commentRepository,
                           UserRepository userRepository) {
        this.commentDTOMapper = commentDTOMapper;
        this.commentsDTOMapper = commentsDTOMapper;
        this.createOrUpdateCommentDTOMapper = createOrUpdateCommentDTOMapper;
        this.adRepository = adRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    private boolean isAdmin(String email) {
        return userRepository.findByEmail(email)
                .map(user -> user.getRole() == Role.ADMIN)
                .orElse(false);
    }

    public boolean isOwner(Integer commentId, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoUsersFoundByEmailException(email));

        return commentRepository.findById(commentId)
                .map(comment -> comment.getAuthorId().equals(user.getId()))
                .orElse(false);
    }

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

    public void deleteComment(Integer adId,
                              Integer commentId,
                              String email) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(NoCommentsException::new);

        if (!comment.getAd().getId().equals(adId)) {
            throw new IllegalArgumentException("Комментарий не принадлежит объявлению с id " + adId);
        }

        if (!isOwner(commentId, email)&& !isAdmin(email)) {
            throw new NoneOfYourBusinessException("Вы не можете удалить этот комментарий");
        }

        commentRepository.delete(comment);
    }

    public Comment updateComment(Integer adId,
                                 Integer commentId,
                                 CreateOrUpdateComment updateComment,
                                 String email) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(NoCommentsException::new);

        if (!comment.getAd().getId().equals(adId)) {
            throw new CommentDoesNotBelongToAdException(commentId, adId);
        }

        if (!isOwner(commentId, email) && !isAdmin(email)) {
            throw new NoneOfYourBusinessException("Вы не можете отредактировать этот комментарий");
        }

        createOrUpdateCommentDTOMapper.updateEntityFromDto(updateComment, comment);
        return commentDTOMapper.toDto(comment);
    }
}
