package ru.skypro.homework.controller;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.service.impl.UserImageService;
import ru.skypro.homework.service.impl.UserService;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private UserImageService userImageService;

    @Test
    @WithMockUser(username = "user@gmail.com")
    void setPassword() throws Exception {

        JSONObject json = new JSONObject();
        json.put("currentPassword", "oldPass123");
        json.put("newPassword", "newPass123");

        when(userService.updatePassword(any(NewPassword.class), anyString())).thenReturn(true);

        mockMvc.perform(post("/users/set_password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString())
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void getUser() throws Exception {
        User user = new User(123, "email@mail.ru", "firstName", "lastName", "89998887766", Role.USER, "image");
        when(userService.getUser(anyString())).thenReturn(user);

        mockMvc.perform(get("/users/me")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                .andExpect(jsonPath("$.phone").value(user.getPhone()))
                .andExpect(jsonPath("$.image").value(user.getImage()));
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void updateUser() throws Exception {
        UpdateUser updateUser = new UpdateUser("bob", "newby", "+79998887766");
        when(userService.updateUser(any(UpdateUser.class), anyString())).thenReturn(updateUser);

        JSONObject json = new JSONObject();
        json.put("firstName", "alexa");
        json.put("lastName", "play");
        json.put("phone", "+7 964 341-33-43");

        mockMvc.perform(patch("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString())
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(updateUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(updateUser.getLastName()))
                .andExpect(jsonPath("$.phone").value(updateUser.getPhone()));
    }

    @Test
    @WithMockUser(username = "user@gmail.com")
    void updateUserImage() throws Exception {
        when(userService.updateUserImage(any(MultipartFile.class), anyString())).thenReturn(true);

        MockMultipartFile image = new MockMultipartFile(
                "image",
                "avatar.jpg",
                "image/jpeg",
                "test-image-content".getBytes()
        );
        mockMvc.perform(multipart("/users/me/image")
                        .file(image)
                        .with(csrf())
                        .with(request -> { request.setMethod("PATCH"); return request; }))
                .andExpect(status().isOk());
    }
}