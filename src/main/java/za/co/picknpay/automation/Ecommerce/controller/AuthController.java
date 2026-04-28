package za.co.picknpay.automation.Ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.picknpay.automation.Ecommerce.dto.LoginRequest;
import za.co.picknpay.automation.Ecommerce.security.JwtUtil;

import java.util.Map;

@RestController
@RequestMapping("/api/oms")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody LoginRequest loginRequest) {
        // This validates against our hardcoded 'thapelo_oms' user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        if (authentication.isAuthenticated()) {
            String token = jwtUtil.generateToken(loginRequest.getUsername());
            return ResponseEntity.ok(Map.of("accessToken", token));
        } else {
            return ResponseEntity.status(401).body("Invalid OMS Credentials");
        }
    }
}