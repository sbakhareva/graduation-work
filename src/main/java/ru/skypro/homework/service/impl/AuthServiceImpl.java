package ru.skypro.homework.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.mappers.RegisterDTOMapper;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.model.UserImage;
import ru.skypro.homework.repository.UserImageRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdsOnlineUserDetailsService;
import ru.skypro.homework.service.AuthService;
import ru.skypro.homework.service.UserImageService;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Value("${default.user.image.path}")
    private String defaultUserImagePath;

    private final AdsOnlineUserDetailsService adsOnlineUserDetailsService;
    private final PasswordEncoder encoder;
    private final RegisterDTOMapper registerDTOMapper = new RegisterDTOMapper();
    private final UserRepository userRepository;
    private final UserImageService userImageService;
    private final UserImageRepository userImageRepository;

    public AuthServiceImpl(AdsOnlineUserDetailsService adsOnlineUserDetailsService,
                           PasswordEncoder passwordEncoder,
                           UserRepository userRepository,
                           UserImageService userImageService,
                           UserImageRepository userImageRepository) {
        this.adsOnlineUserDetailsService = adsOnlineUserDetailsService;
        this.encoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userImageService = userImageService;
        this.userImageRepository = userImageRepository;
    }

    @Override
    public boolean login(String userName, String password) {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(userName);
        if (optionalUser.isEmpty()) {
            return false;
        }
        if (!userRepository.existsByEmail(userName)) {
            return false;
        }

        UserEntity user = optionalUser.get();
        return encoder.matches(password, user.getPassword());
    }

    @Override
    public boolean register(Register register) {
        if (userRepository.existsByEmail(register.getUsername())) {
            return false;
        }

        UserEntity user = registerDTOMapper.fromDTO(register, encoder);

        UserImage defaultImage = new UserImage();
        defaultImage.setFilePath(defaultUserImagePath);
        defaultImage.setFileSize(0);
        defaultImage.setMediaType("image/jpeg");
        defaultImage.setUser(user);

        user.setImage(defaultImage);
        userRepository.save(user);

        return true;
    }
}
