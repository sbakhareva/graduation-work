package ru.skypro.homework.model;

import jakarta.persistence.Entity;
import lombok.*;
import ru.skypro.homework.dto.Role;

@Entity
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
