package ru.skypro.homework.mappers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.model.UserImage;

@Component
public class RegisterDTOMapper {

    @Value("${users-images/default-user-image.jpeg}")
    private UserImage defaultUserImage;

    public UserEntity fromDTO(Register register, PasswordEncoder passwordEncoder) {
        return UserEntity.builder()
                .email(register.getUsername())
                .firstName(register.getFirstName())
                .lastName(register.getLastName())
                .image(defaultUserImage)
                .phone(register.getPhone())
                .password(passwordEncoder.encode(register.getPassword()))
                .role(register.getRole())
                .enabled(true)
                .build();
    }
}
