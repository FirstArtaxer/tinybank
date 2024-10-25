package com.artaxer.tinybank.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Data
public class UserAccount implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstname;
    private String lastname;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    private String roles;
    @Column(nullable = false, unique = true)
    private String accountNumber;
    private Boolean isActive = true;

    public UserAccount() {
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(roles.split(",")).map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList());
    }

    public List<Role> getRoles() {
        return Arrays.stream(roles.split(",")).map(role -> Role.valueOf(role)).collect(Collectors.toList());
    }

    public void setRoles(List<Role> roles) {
        this.roles = compactRoles(roles);
    }

    private String compactRoles(List<Role> roles) {
        return roles.stream().map(role -> role.name()).collect(Collectors.joining(","));
    }


    public UserAccount(String firstname, String lastname, String username, String password, List<Role> roles, Boolean isActive) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.roles = compactRoles(roles);
        this.isActive = isActive;
        this.accountNumber = UUID.randomUUID().toString();
    }
}
