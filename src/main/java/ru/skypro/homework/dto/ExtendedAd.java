package ru.skypro.homework.dto;

import lombok.*;
import ru.skypro.homework.model.AdEntity;

/**
 * ДТО для сущности {@link AdEntity} для передачи данных между сервером и пользователем.
 * Содержит расширенную информацию об объявлении:
 * <ul>
 *     <li>{@link #pk} — идентификатор объявления</li>
 *     <li>{@link #authorFirstName} — имя автора объявления</li>
 *     <li>{@link #authorLastName} — фамилия автора объявления</li>
 *     <li>{@link #description} — текст объявления</li>
 *     <li>{@link #email} — email автора объявления</li>
 *    <li>{@link #image} — путь к эндпоинту для получения байтов фото из файловой системы</li>
 *    <li>{@link #phone} — телефон автора объявления</li>
 *    <li>{@link #price} — цена</li>
 *    <li>{@link #title} — заголовок объявления</li>
 *  </ul>
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtendedAd {
    private Integer pk;
    private String authorFirstName;
    private String authorLastName;
    private String description;
    private String email;
    private String image;
    private String phone;
    private Integer price;
    private String title;
}
