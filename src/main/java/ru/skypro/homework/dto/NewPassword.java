package ru.skypro.homework.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ДТО для обновления пароля текущего пользователя.
 * Содержит текущий пароль пользователя и новый
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewPassword {

    @Size(min = 8, max = 16)
    private String currentPassword;
    @Size(min = 8, max = 16)
    private String newPassword;
}
