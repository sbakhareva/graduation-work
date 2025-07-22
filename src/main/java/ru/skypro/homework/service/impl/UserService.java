package ru.skypro.homework.service.impl;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
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

/**
 * Сервис для работы с сущностью {@link UserEntity}.
 * Содержит методы для получения и обновления информации о пользователях.
 */

@Service
@Transactional
@AllArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserDTOMapper userDTOMapper;
    private final UpdateUserDTOMapper updateUserDTOMapper;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserImageRepository userImageRepository;
    private final UserImageService userImageService;

    /**
     * Обновляет пароль текущего пользователя
     *
     * @param newPassword объект {@link NewPassword}, содержащий текущий и новый пароли
     * @param email       email пользователя, полученное из объекта {@link Authentication}
     * @return {@code boolean} результат операции:
     * <ul>
     *   <li><b>true</b> – если пароль успешно обновлен</li>
     *   <li><b>false</b> – если введенный старый пароль и текущий пароли не совпадают</li>
     * </ul>
     *  @throws NoUsersFoundByEmailException, если пользователь с указанным email не найден
     */
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

    /**
     * Получает информацию о пользователе
     *
     * @param email email пользователя, извлеченный из {@link Authentication}
     * @return объект ДТО {@link User}
     * @throws NoUsersFoundByEmailException, если пользователем с указанным email не найден
     */
    @Transactional(readOnly = true)
    public User getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoUsersFoundByEmailException(email));

        return userDTOMapper.toDTO(userEntity);
    }

    /**
     * Обновляет информацию о пользователе
     *
     * @param updateUser объект ДТО {@link UpdateUser}, содержащий информацию для обновления полей сущности
     * @param email      email пользователя, извлеченный из {@link Authentication}
     * @return объект ДТО {@link UpdateUser} с уже обновленными полями из сущности {@link UserEntity}
     * @throws NoUsersFoundByEmailException, если пользователем с указанным email не найден
     */
    public UpdateUser updateUser(UpdateUser updateUser, String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoUsersFoundByEmailException(email));

        return updateUserDTOMapper.updateEntityFromDto(updateUser, userEntity);
    }

    /**
     * Загружает новую картинку пользователя вместо дефолтной или текущей.
     * Изначально осуществляется проверка, существует ли в таблице 'user_images' фото с id текущего пользователя.
     * Если фото существует, оно удаляется из файловой системы и из базы данных.
     * Если у пользователя пока нет фото профиля,
     * новое фото загружается в базу данных и сохраняется в файловой системе.
     * @param newImage новое фото, загруженное пользователем, которое преобразуется в сущность {@link UserImage}
     * @param email email пользователя, извлеченный из {@link Authentication}
     * @return {@code boolean} результат операции:
     * <ul>
     *   <li><b>true</b> – если фото успешно загружено</li>
     *   <li><b>false</b> – если при загрузке возникли ошибки и фото не было обновлено</li>
     * </ul>
     * @throws NoUsersFoundByEmailException, если пользователем с указанным email не найден
     * @throws NoImagesFoundException, если не найдены фото для текущего пользователя
     */
    public boolean updateUserImage(MultipartFile newImage, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoUsersFoundByEmailException(email));

        if (userImageRepository.existsByUserId(user.getId())) {
            userImageService.deleteImageFile(user.getImage().getId());
            try {
                userImageRepository.deleteByUserId(user.getId());
                logger.info("Старое изображение пользователя {} удалено", email);
            } catch (Exception e) {
                logger.error("Ошибка при удалении фото пользователя с id {}: {}", user.getId(), e.getMessage(), e);
            }

            try {
                userImageService.uploadImage(user.getId(), newImage);
            } catch (IOException e) {
                logger.error("Ошибка при загрузке фото пользователя с id {}: {}", user.getId(), e.getMessage(), e);
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
            userImageService.uploadImage(user.getId(), newImage);
        } catch (IOException e) {
            logger.error("Ошибка при загрузке фото пользователя с id {}: {}", user.getId(), e.getMessage(), e);
        }
        UserImage image = userImageRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoImagesFoundException("Не найдено фото для пользователя с id " + user.getId()));
        user.setImage(image);
        return true;
    }
}
