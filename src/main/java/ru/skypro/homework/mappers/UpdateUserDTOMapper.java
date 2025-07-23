package ru.skypro.homework.mappers;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.model.UserEntity;

/**
 * Маппер для обновления полей сущности {@link UserEntity}
 * с использованием объекта {@link UpdateUser}
 */
@Component
public class UpdateUserDTOMapper {

    private final UserDTOMapper userDTOMapper = new UserDTOMapper();

    public UpdateUser updateEntityFromDto(UpdateUser updateUser, UserEntity user) {

        user.setFirstName(updateUser.getFirstName());
        user.setLastName(updateUser.getLastName());
        user.setPhone(updateUser.getPhone());

        User updatedUser = userDTOMapper.toDTO(user);

        return new UpdateUser(
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getPhone()
        );
    }
}
