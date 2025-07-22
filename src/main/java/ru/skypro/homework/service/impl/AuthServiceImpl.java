package ru.skypro.homework.service.impl;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.Login;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.exception.NoUsersFoundByEmailException;
import ru.skypro.homework.mappers.RegisterDTOMapper;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AuthService;

/**
 * Сервис для регистрации и аутентификации пользователей.
 * Содержит методы для сохранения новых пользователей в базу данных
 */

@Service
@Transactional
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final PasswordEncoder encoder;
    private final RegisterDTOMapper registerDTOMapper = new RegisterDTOMapper();
    private final UserRepository userRepository;

    /**
     * Выполняет вход в профиль пользователя
     *
     * @param username email пользователя, полученный из ДТО {@link Login}
     * @param password пароль пользователя, полученный из ДТО {@link Login}
     * @return {@code boolean} результат операции:
     * <ul>
     *   <li><b>true</b> – если авторизация прошла успешно</li>
     *   <li><b>false</b> – если пользователь не существует или не совпадает пароль</li>
     * </ul>
     */
    @Override
    public boolean login(String username, String password) {
        if (!userRepository.existsByEmail(username)) {
            return false;
        }

        UserEntity user = userRepository.findByEmail(username).get();
        return encoder.matches(password, user.getPassword());
    }

    /**
     * Сохраняет нового пользователя в базу данных.
     *
     * @param register ДТО {@link Register}, содержащий основные данные о пользователе
     * @return {@code boolean} результат операции:
     * <ul>
     *   <li><b>true</b> – если регистрация прошла успешно и пользователь сохранен в базу данных</li>
     *   <li><b>false</b> – если пользователь с таким именем пользователя уже существует</li>
     * </ul>
     */
    @Override
    public boolean register(Register register) {
        if (userRepository.existsByEmail(register.getUsername())) {
            logger.info("Пользователь с именем пользователя {} уже существует", register.getUsername());
            return false;
        }

        UserEntity user = registerDTOMapper.fromDTO(register, encoder);

        userRepository.save(user);
        logger.info("Пользователь {} успешно добавлен", user.getEmail());
        return true;
    }
}
