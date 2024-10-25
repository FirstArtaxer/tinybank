package com.artaxer.tinybank.controller;

import com.artaxer.tinybank.dto.UserLoginDto;
import com.artaxer.tinybank.dto.UserRegisterDto;
import com.artaxer.tinybank.dto.UserTokenDto;
import com.artaxer.tinybank.dto.GlobalResponse;
import com.artaxer.tinybank.model.UserAccount;
import com.artaxer.tinybank.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public GlobalResponse registerUser(@RequestBody UserRegisterDto userRegisterDto) {
        UserAccount newUser = userService.createUserAccount(userRegisterDto);
        return new GlobalResponse(newUser.getUsername() + " has been registered");
    }

    @PostMapping("/login")
    public UserTokenDto loginUser(@RequestBody UserLoginDto userLoginDto) {
        return userService.login(userLoginDto);
    }

    @PatchMapping("/activate")
    public GlobalResponse activateUser(@RequestBody UserLoginDto userLoginDto) {
        return userService.activate(userLoginDto);
    }
}
