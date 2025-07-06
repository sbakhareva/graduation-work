package ru.skypro.homework.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.exception.NoImagesFoundException;
import ru.skypro.homework.exception.NoUsersFoundException;
import ru.skypro.homework.mappers.UpdateUserDTOMapper;
import ru.skypro.homework.mappers.UserDTOMapper;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.model.UserImage;
import ru.skypro.homework.repository.UserImageRepository;
import ru.skypro.homework.repository.UserRepository;

import java.io.IOException;

@Service
public class UserService {

    private final UserDTOMapper userDTOMapper = new UserDTOMapper();
    private final UpdateUserDTOMapper updateUserDTOMapper = new UpdateUserDTOMapper();

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserImageRepository userImageRepository;
    private final UserImageService userImageService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserImageRepository userImageRepository, UserImageService userImageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userImageRepository = userImageRepository;
        this.userImageService = userImageService;
    }

    public boolean updatePassword(NewPassword newPassword) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!passwordEncoder.matches(newPassword.getCurrentPassword(), userEntity.getPassword())) {
            return false;
        }

        userEntity.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
        userRepository.save(userEntity);

        return true;
    }

    public User getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoUsersFoundException("Пользователей с именем пользователя " + email + " не найдено"));
        return userDTOMapper.toDTO(userEntity);
    }

    public UpdateUser updateUser(UpdateUser updateUser, String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoUsersFoundException("Пользователей с именем пользователя " + email + " не найдено"));

        return updateUserDTOMapper.updateEntityFromDto(updateUser, userEntity);
    }

    public boolean updateUserImage(MultipartFile newImage, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoUsersFoundException("Пользователей с именем пользователя " + email + " не найдено"));

        userImageRepository.deleteByUserId(user.getId());

        try {
            userImageService.uploadUserImage(user.getId(), newImage);
            UserImage image = userImageRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new NoImagesFoundException("Не найдено фото для пользователя с id " + user.getId()));
            user.setImage(image);
            return true;
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке изображения: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Произошла ошибка: " + e.getMessage());
            return false;
        }
    }
} 