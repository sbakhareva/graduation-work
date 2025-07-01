package ru.skypro.homework.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class CreateOrUpdateComment {

    @Size(min = 8, max = 64)
    private String text;

    // Метод для преобразования из сущности в DTO
    public static CreateOrUpdateComment fromEntity(ru.skypro.homework.model.Comment commentEntity) {
        if (commentEntity == null) {
            return null;
        }
        CreateOrUpdateComment createOrUpdateComment = new CreateOrUpdateComment();
        createOrUpdateComment.setText(commentEntity.getText());
        return createOrUpdateComment;
    }

    // Метод для преобразования из DTO в сущность
    public static ru.skypro.homework.model.Comment toEntity(CreateOrUpdateComment commentDto) {
        if (commentDto == null) {
            return null;
        }
        ru.skypro.homework.model.Comment commentEntity = new ru.skypro.homework.model.Comment();
        commentEntity.setText(commentDto.getText());
        // Установка других полей, таких как authorId и createdAt, может быть добавлена здесь, если необходимо
        return commentEntity;
    }
}
