package ru.skypro.homework.dto;

import lombok.*;
import ru.skypro.homework.model.UserEntity;

/**
 * ДТО для сущности {@link UserEntity} для передачи данных между сервером и пользователем.
 * Содержит основные данные о пользователе:
 * <ul>
 *     <li>{@link #id} — уникальный идентификатор пользователя</li>
 *     <li>{@link #email} — email в формате example@domain.com (уникальный)</li>
 *    <li>{@link #firstName} — имя пользователя</li>
 *    <li>{@link #lastName} — фамилия пользователя</li>
 *    <li>{@link #phone} — номер телефона</li>
 *    <li>{@link #role} — роль пользователя {@link Role}</li>
 *    <li>{@link #image} — путь к эндпоинту для получения байтов фото из файловой системы</li>
 *  </ul>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class User {

    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private Role role;
    private String image; // ссылка на аватар пользователя
}
