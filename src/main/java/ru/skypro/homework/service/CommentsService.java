package ru.skypro.homework.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
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
        return createOrUpdateCommentDTOMapper.createEntityFromDto(createOrUpdateComment, user, ad);
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
        AdEntity ad = adRepository.findById(adId)
                .orElseThrow(() -> new NoAdsFoundException(adId));

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
