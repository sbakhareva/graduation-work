package ru.skypro.homework.mappers;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.model.UserImage;

import static ru.skypro.homework.utils.ImageURLGenerator.generateImageUrl;

/**
 * Маппер UserEntity <-> User
 */
@Component
public class UserDTOMapper {

    public UserEntity fromDto(User user, UserImage userImage) {
        if (user == null) {
            return null;
        }
        return UserEntity.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .role(user.getRole())
                .image(userImage) // та же ситуация, что и в AdDTOMapper
                .build();
    }

    public User toDTO(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        return new User(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getPhone(),
                userEntity.getRole(),
                generateImageUrl(userEntity)
        );
    }
}


