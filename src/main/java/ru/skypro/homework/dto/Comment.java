package ru.skypro.homework.dto;

import lombok.*;
import ru.skypro.homework.model.CommentEntity;

/**
 * ДТО для сущности {@link CommentEntity} для передачи данных между сервером и пользователем.
 * Содержит основные данные для отображения:
 * <ul>
 *     <li>{@link #author} — идентификатор автора комментария</li>
 *     <li>{@link #authorImage} — ссылка на аватар пользователя в файловой системе</li>
 *     <li>{@link #authorFirstName} — имя автора комментария</li>
 *     <li>{@link #createdAt} — дата и время создания комментария в миллисекундах с 00:00:00 01.01.1970</li>
 *    <li>{@link #pk} — идентификатор комментария</li>
 *    <li>{@link #text} — текст комментария</li>
 *  </ul>
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    private Integer author; // id автора комментария
    private String authorImage;
    private String authorFirstName;
    private long createdAt;
    private Integer pk; // id комментария
    private String text;
}
