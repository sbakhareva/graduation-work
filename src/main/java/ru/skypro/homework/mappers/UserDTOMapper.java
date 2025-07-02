package ru.skypro.homework.mappers;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.UserDTO;
import ru.skypro.homework.model.User;

import static ru.skypro.homework.utils.ImageURLGenerator.generateImageUrl;

@Component
public class UserDTOMapper {

    public User fromDto(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        return User.builder()
                .email(userDTO.getEmail())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .phone(userDTO.getPhone())
                .role(userDTO.getRole())
                // тут будет преобразование ссылки на аватар в аватар
                .build();
    }

    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getRole(),
                generateImageUrl(user)
        );
    }
}


