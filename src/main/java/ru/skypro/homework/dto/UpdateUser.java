package ru.skypro.homework.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.skypro.homework.model.UserEntity;

/**
 * ДТО для обновления данных существующего пользователя {@link UserEntity}.
 * Содержит имя и фамилию пользователя и номер телефона
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class UpdateUser {

    @Size(min = 2, max = 10)
    private String firstName;
    @Size(min = 2, max = 10)
    private String lastName;
    @Pattern(regexp = "\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}")
    private String phone;
}
