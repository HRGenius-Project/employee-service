package com.hrgenius.employee_service.client;

import com.hrgenius.employee_service.dto.InternalCreateUserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {

    @PostMapping("/internal/users")
    void createInternalUser(
            @RequestHeader("X-Internal-Key") String internalApiKey,
            @RequestBody InternalCreateUserRequest request
    );
}