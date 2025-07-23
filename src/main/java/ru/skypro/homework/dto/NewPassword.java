package ru.skypro.homework.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * ДТО для обновления пароля текущего пользователя.
 * Содержит текущий пароль пользователя и новый
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class NewPassword {

    @Size(min = 8, max = 16)
    private String currentPassword;
    @Size(min = 8, max = 16)
    private String newPassword;
}
