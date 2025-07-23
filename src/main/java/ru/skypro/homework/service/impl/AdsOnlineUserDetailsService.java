package ru.skypro.homework.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.CustomUserDetails;

@Service
public class AdsOnlineUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AdsOnlineUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден."));

        return new CustomUserDetails(
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.isEnabled()
        );
    }
}
