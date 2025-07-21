package ru.skypro.homework.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.exception.NoImagesFoundException;
import ru.skypro.homework.exception.NoUsersFoundByEmailException;
import ru.skypro.homework.mappers.UpdateUserDTOMapper;
import ru.skypro.homework.mappers.UserDTOMapper;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.model.UserImage;
import ru.skypro.homework.repository.UserImageRepository;
import ru.skypro.homework.repository.UserRepository;

import java.io.IOException;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserDTOMapper userDTOMapper;
    private final UpdateUserDTOMapper updateUserDTOMapper;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserImageRepository userImageRepository;
    private final UserImageService userImageService;

    public UserService(UserDTOMapper userDTOMapper,
                       UpdateUserDTOMapper updateUserDTOMapper,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserImageRepository userImageRepository,
                       UserImageService userImageService) {
        this.userDTOMapper = userDTOMapper;
        this.updateUserDTOMapper = updateUserDTOMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userImageRepository = userImageRepository;
        this.userImageService = userImageService;
    }

    public boolean updatePassword(NewPassword newPassword, String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoUsersFoundByEmailException(email));

        if (!passwordEncoder.matches(newPassword.getCurrentPassword(), userEntity.getPassword())) {
            logger.warn("Текущий и старый введённые пароли не совпадают.");
            return false;
        }

        userEntity.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
        logger.info("Пароль успешно обновлен.");
        return true;
    }

    @Transactional(readOnly = true)
    public User getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoUsersFoundByEmailException(email));

        return userDTOMapper.toDTO(userEntity);
    }

    public UpdateUser updateUser(UpdateUser updateUser, String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoUsersFoundByEmailException(email));

        return updateUserDTOMapper.updateEntityFromDto(updateUser, userEntity);
    }

    @Transactional
    public boolean updateUserImage(MultipartFile newImage, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoUsersFoundByEmailException(email));

        if (userImageRepository.existsByUserId(user.getId())) {
            userImageService.deleteUserImageFile(user.getImage().getId());
            try {
                userImageRepository.deleteByUserId(user.getId());
                logger.info("Старое изображение пользователя {} удалено", email);
            } catch (Exception e) {
                logger.error("Ошибка при удалении фото пользователя с id {}: {}", user.getId(), e.getMessage(), e);
            }

            try {
                userImageService.uploadUserImage(user.getId(), newImage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                UserImage image = userImageRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new NoImagesFoundException("Не найдено фото для пользователя с id " + user.getId()));
                user.setImage(image);
                return true;
            } catch (Exception e) {
                logger.error("Произошла ошибка: {}", e.getMessage());
                return false;
            }
        }
        try {
            userImageService.uploadUserImage(user.getId(), newImage);
        } catch (IOException e) {
            logger.error("Ошибка при сохранении изображения");
        }
        UserImage image = userImageRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoImagesFoundException("Не найдено фото для пользователя с id " + user.getId()));
        user.setImage(image);
        return true;
    }
}
