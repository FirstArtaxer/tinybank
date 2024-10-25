package com.artaxer.tinybank.service;


import com.artaxer.tinybank.dto.UserAccountDto;
import com.artaxer.tinybank.dto.UserLoginDto;
import com.artaxer.tinybank.dto.UserRegisterDto;
import com.artaxer.tinybank.dto.UserTokenDto;
import com.artaxer.tinybank.exception.BadRequestException;
import com.artaxer.tinybank.exception.NotFoundException;
import com.artaxer.tinybank.dto.GlobalResponse;
import com.artaxer.tinybank.model.Role;
import com.artaxer.tinybank.model.UserAccount;
import com.artaxer.tinybank.model.repository.UserRepository;
import com.artaxer.tinybank.security.JwtManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepo;
    private final JwtManager jwtManager;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo,
                       JwtManager jwtManager,
                       PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.jwtManager = jwtManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserAccount loadUserByUsername(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found with username: " + username));
    }

    public UserAccount loadUserByAccountNumber(String accountNumber) {
        var user = userRepo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("user account not found with account number: " + accountNumber));

        if (!user.getIsActive())
            throw new NotFoundException("user account is deactivated: " + accountNumber);

        return user;
    }

    public UserAccount loadActiveUserByUsername(String username) {
        var user = userRepo.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found with username: " + username));
        if (!user.getIsActive())
            throw new BadRequestException("User is deactivated!");

        return user;
    }

    public UserAccount createUserAccount(UserRegisterDto userRegisterDto) {
        userRepo.findByUsername(userRegisterDto.username())
                .ifPresent(userAccount -> {
                    throw new BadRequestException("username already exists");
                });
        String encodedPassword = passwordEncoder.encode(userRegisterDto.password());
        return userRepo.save(
                new UserAccount(
                        userRegisterDto.firstName(),
                        userRegisterDto.lastName(),
                        userRegisterDto.username(),
                        encodedPassword,
                        List.of(Role.USER),
                        true)
        );
    }

    public GlobalResponse deactivateUserAccount(String accountNumber) {
        var user = loadUserByAccountNumber(accountNumber);
        user.setIsActive(false);
        userRepo.save(user);
        return new GlobalResponse("user account deactivated!");
    }

    public UserTokenDto login(UserLoginDto userLoginDto) {
        var user = loadActiveUserByUsername(userLoginDto.username());
        if (!passwordEncoder.matches(userLoginDto.password(), user.getPassword())) {
            throw new BadRequestException("Invalid username or password");
        }
        var token = jwtManager.generateToken(user);
        return new UserTokenDto(token);
    }

    public GlobalResponse activate(UserLoginDto userLoginDto) {
        var user = loadUserByUsername(userLoginDto.username());
        if (!passwordEncoder.matches(userLoginDto.password(), user.getPassword())) {
            throw new BadRequestException("Invalid username or password");
        }
        if (user.getIsActive()) {
            throw new BadRequestException("user account is already activated");
        }
        user.setIsActive(true);
        userRepo.save(user);
        return new GlobalResponse("user account is activated!");
    }

    public Page<UserAccountDto> getUserAccounts(Pageable pageable) {
        return userRepo.findAll(pageable)
                .map(userAccount -> new UserAccountDto(userAccount.getUsername(), userAccount.getAccountNumber()));
    }
}
