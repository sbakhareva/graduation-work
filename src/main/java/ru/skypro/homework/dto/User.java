package ru.skypro.homework.dto;

import lombok.*;

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

    // Метод для преобразования из сущности в DTO
    public static User fromEntity(ru.skypro.homework.model.User userEntity) {
        if (userEntity == null) {
            return null;
        }
        User userDto = new User();
        userDto.setId(userEntity.getId());
        userDto.setEmail(userEntity.getEmail());
        userDto.setFirstName(userEntity.getFirstName());
        userDto.setLastName(userEntity.getLastName());
        userDto.setPhone(userEntity.getPhone());
        userDto.setRole(userEntity.getRole()); // Предполагается, что Role - это перечисление или класс
        userDto.setImage(userEntity.getImage());
        return userDto;
    }

    // Метод для преобразования из DTO в сущность
    public static ru.skypro.homework.model.User toEntity(User userDto) {
        if (userDto == null) {
            return null;
        }
        ru.skypro.homework.model.User userEntity = new ru.skypro.homework.model.User();
        userEntity.setId(userDto.getId());
        userEntity.setEmail(userDto.getEmail());
        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());
        userEntity.setPhone(userDto.getPhone());
        userEntity.setRole(userDto.getRole()); // Предполагается, что Role - это перечисление или класс
        userEntity.setImage(userDto.getImage());
        return userEntity;
    }
}
