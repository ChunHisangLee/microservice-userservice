package com.jack.userservice.client;

import com.jack.userservice.dto.AuthRequestDTO;
import com.jack.userservice.dto.AuthResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service", url = "http://127.0.0.1:8084")
public interface AuthServiceClient {

    @PostMapping("/api/auth/login")
    AuthResponseDTO login(@RequestBody AuthRequestDTO authRequestDTO);

    @PostMapping("/api/auth/logout")
    void logout(@RequestHeader("Authorization") String token);

    @PostMapping("/api/auth/validate")
    Boolean validateToken(@RequestHeader("Authorization") String token, @RequestParam Long userId);

}
