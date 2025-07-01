package ru.skypro.homework.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Login {

    @Size(min = 4, max = 32)
    private String username;
    @Size(min = 8, max = 16)
    private String password;
}
