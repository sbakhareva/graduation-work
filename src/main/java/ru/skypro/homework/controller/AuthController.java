package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.skypro.homework.dto.Login;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.service.AuthService;

@CrossOrigin(value = "http://localhost:3000")
@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Авторизация существующего пользователя
     *
     * @param login объект ДТО {@link Login}, содержащий имя пользователя и пароль
     * @return {@link ResponseEntity}:
     * <ul>
     *   <li><b>200 OK</b> – если авторизация прошла успешно</li>
     *   <li><b>401 Unauthorized</b> – если пользователь не существует или не совпадает пароль</li>
     * </ul>
     */
    @PostMapping("/login")
    @Operation(summary = "Авторизация пользователя", tags = {"Авторизация"})
    public ResponseEntity<?> login(@RequestBody Login login) {
        if (authService.login(login.getUsername(), login.getPassword())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Регистрация нового пользователя
     *
     * @param register объект ДТО {@link Register}, содержащий всю основную информацию о пользователе
     * @return {@link ResponseEntity}:
     * <ul>
     *   <li><b>201 Created</b> – если пользователь успешно сохранен в базу данных</li>
     *   <li><b>404 BadRequest</b> – если переданы данные, отличающиеся от шаблона</li>
     * </ul>
     */
    @PostMapping("/register")
    @Operation(summary = "Регистрация пользователя", tags = {"Регистрация"})
    public ResponseEntity<?> register(@RequestBody Register register) {
        if (authService.register(register)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
