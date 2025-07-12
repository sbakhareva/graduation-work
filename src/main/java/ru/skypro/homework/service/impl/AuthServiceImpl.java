package ru.skypro.homework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.mappers.RegisterDTOMapper;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdsOnlineUserDetailsService;
import ru.skypro.homework.service.AuthService;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AdsOnlineUserDetailsService adsOnlineUserDetailsService;
    private final PasswordEncoder encoder;
    private final RegisterDTOMapper registerDTOMapper = new RegisterDTOMapper();
    private final UserRepository userRepository;

    public AuthServiceImpl(AdsOnlineUserDetailsService adsOnlineUserDetailsService,
                           PasswordEncoder passwordEncoder,
                           UserRepository userRepository) {
        this.adsOnlineUserDetailsService = adsOnlineUserDetailsService;
        this.encoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public boolean login(String username, String password) {
        if (!userRepository.existsByEmail(username)) {
            return false;
        }

        UserEntity user = userRepository.findByEmail(username).get();
        return encoder.matches(password, user.getPassword());
    }

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
