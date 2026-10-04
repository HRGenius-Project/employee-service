package com.hrgenius.employee_service.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternalCreateUserRequest {
    private String username;
    private String email;
    private String temporaryPassword;
    private Set<String> roles;
}