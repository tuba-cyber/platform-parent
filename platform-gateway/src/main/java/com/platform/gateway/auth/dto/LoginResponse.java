package com.platform.gateway.auth.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private String username;
    private String fullName;
    private String companyId;
    private String companyName;
    private List<String> roles;
    private List<String> permissions;
}