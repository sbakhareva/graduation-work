package ru.skypro.homework.mappers;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.CommentDTO;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.model.Comment;

import java.util.Collections;
import java.util.List;

@Component
public class CommentsDTOMapper {

    private final CommentDTOMapper commentDTOMapper;

    public CommentsDTOMapper(CommentDTOMapper commentDTOMapper) {
        this.commentDTOMapper = commentDTOMapper;
    }

    public Comments toDto(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return new Comments(0, Collections.EMPTY_LIST);
        }

        List<CommentDTO> commentsDto = comments.stream()
                .map(commentDTOMapper::toDto)
                .toList();

        return new Comments(commentsDto.size(), commentsDto);
    }
}
