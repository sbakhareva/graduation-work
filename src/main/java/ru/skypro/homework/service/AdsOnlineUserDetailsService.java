package ru.skypro.homework.service;

import org.hibernate.Hibernate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.skypro.homework.model.UserEntity;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.utils.CustomUserDetails;

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

        if (Hibernate.isInitialized(user.getImage()) && user.getImage() != null) {
            Hibernate.initialize(user.getImage());
        }


        return new CustomUserDetails(
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.isEnabled()
        );
    }
}
