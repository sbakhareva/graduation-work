package ru.skypro.homework.dto;

import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.Authentication;
import ru.skypro.homework.model.CommentEntity;

/**
 * ДТО для сущности {@link CommentEntity} для создания новых комментариев или обновления существующих.
 * Содержит текст комментария
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class CreateOrUpdateComment {

    @Size(min = 8, max = 64)
    private String text;
}
