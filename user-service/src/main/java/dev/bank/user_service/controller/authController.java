package dev.bank.user_service.controller;


import dev.bank.user_service.JwtService;
import dev.bank.user_service.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class authController {
    

    private final JwtService jwtService;
    
   
    @PostMapping("/token")
    public Map<String , String> token(@RequestBody Map<String, String> userDetails) {
        String userId = userDetails.get("userId");
        String role = userDetails.get("role");
        String token = jwtService.generateToken(userId, role);
        return Map.of("access_token", token , "token_type", "bearer");

    }
      
    
    
}
