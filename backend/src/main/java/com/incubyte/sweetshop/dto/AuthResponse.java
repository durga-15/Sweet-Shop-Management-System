package com.incubyte.sweetshop.dto;

import com.incubyte.sweetshop.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private String email;
    private User.Role role;
}

