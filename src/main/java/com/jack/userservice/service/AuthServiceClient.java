package com.jack.userservice.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service", url = "http://auth-service:8081") // Assuming auth-service runs on 8081
public interface AuthServiceClient {

    @PostMapping("/api/auth/generate-token")
    String generateToken(@RequestParam String email);  // Make an API request to generate a token
}
