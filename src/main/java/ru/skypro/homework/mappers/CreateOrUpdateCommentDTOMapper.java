package ru.skypro.homework.mappers;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.CommentDTO;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.model.Comment;

@Component
public class CreateOrUpdateCommentDTOMapper {

    public CommentDTO fromDto(CreateOrUpdateComment comment,
                              Integer authorId,
                              String authorImage,
                              String authorName,
                              Integer createdAt) {
        return null; // нужны репозитории
    }
}
