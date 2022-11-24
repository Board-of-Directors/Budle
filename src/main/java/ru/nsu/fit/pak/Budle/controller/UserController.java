package ru.nsu.fit.pak.Budle.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.fit.pak.Budle.dto.UserDto;
import ru.nsu.fit.pak.Budle.service.UserServiceImpl;

import java.util.List;

@RestController
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class UserController {
    @Autowired
    private UserServiceImpl userService;


    @GetMapping("/users")
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @PostMapping(value = "/register", consumes = {"application/json"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto register(@RequestBody UserDto userDto) {
        return userService.registerUser(userDto);

    }

    @PostMapping(value = "/login", consumes = {"application/json"})
    public UserDto login(@RequestBody UserDto userDto) {
        return userService.loginUser(userDto);
    }

}