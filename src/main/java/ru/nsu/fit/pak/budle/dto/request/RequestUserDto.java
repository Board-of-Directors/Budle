package ru.nsu.fit.pak.budle.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestUserDto {
    @NotNull(message = "Пароль не может быть пустым")
    @Size(min = 6, message = "Пароль не может быть короче 6 символов")
    private String password;
    @NotNull(message = "Имя пользователя не может быть пустым")
    @Size(min = 2, max = 255, message = "Имя пользователя не может быть короче 2 символов или длиннее 255")
    private String username;
    @NotNull(message = "Номер телефона не может быть пустым")
    @Size(min = 11, message = "Номер телефона не может быть короче 11 символов")
    private String phoneNumber;
}