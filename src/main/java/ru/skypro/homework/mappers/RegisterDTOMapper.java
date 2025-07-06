package ru.skypro.homework.mappers;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.model.UserEntity;

@Component
public class RegisterDTOMapper {

    public UserEntity fromDTO(Register register, PasswordEncoder passwordEncoder) {
        return UserEntity.builder()
                .email(register.getUsername())
                .firstName(register.getFirstName())
                .lastName(register.getLastName())
                .phone(register.getPhone())
                .image(null)
                .password(passwordEncoder.encode(register.getPassword()))
                .role(register.getRole())
                .enabled(true)
                .build();
    }
}
