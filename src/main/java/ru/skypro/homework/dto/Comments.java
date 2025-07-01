package ru.skypro.homework.dto;

import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Comments {

    private Integer count;
    private List<Comment> comments;

    // Метод для преобразования из сущности в DTO
    public static Comments fromEntity(List<ru.skypro.homework.model.Comment> commentEntities) {
        if (commentEntities == null) {
            return null;
        }
        Comments commentsDto = new Comments();
        commentsDto.setCount(commentEntities.size());
        commentsDto.setComments(commentEntities.stream()
                .map(Comment::fromEntity) // Используем метод fromEntity из класса Comment
                .collect(Collectors.toList()));
        return commentsDto;
    }

    // Метод для преобразования из DTO в сущность
    public static List<ru.skypro.homework.model.Comment> toEntity(Comments commentsDto) {
        if (commentsDto == null) {
            return null;
        }
        return commentsDto.getComments().stream()
                .map(Comment::toEntity) // Используем метод toEntity из класса Comment
                .collect(Collectors.toList());
    }
}
