package com.jack.userservice.client;

import com.jack.userservice.dto.AuthRequestDTO;
import com.jack.userservice.dto.AuthResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service", url = "http://127.0.0.1:8084")
public interface AuthServiceClient {

    @PostMapping("/api/auth/login")
    AuthResponseDTO login(@RequestBody AuthRequestDTO authRequestDTO);
}
