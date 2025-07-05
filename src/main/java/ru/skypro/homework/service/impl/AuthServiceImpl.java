package ru.skypro.homework.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.mappers.RegisterDTOMapper;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdsOnlineUserDetailsService;
import ru.skypro.homework.service.AuthService;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

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
        userRepository.save(user);
        return true;
    }
}
