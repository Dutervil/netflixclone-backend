package com.wd.netflixcloneback.controller;

import com.wd.netflixcloneback.dto.request.*;
import com.wd.netflixcloneback.dto.response.EmailValidationResponse;
import com.wd.netflixcloneback.dto.response.LoginResponse;
import com.wd.netflixcloneback.dto.response.MessageResponse;
import com.wd.netflixcloneback.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> signup(@Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(authService.signup(userRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest.getEmail(),loginRequest.getPassword()));
    }

    @GetMapping("/validate-email")
    public ResponseEntity<EmailValidationResponse> validateEmail(@RequestParam String email) {
        return ResponseEntity.ok(authService.validateEmail(email));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<MessageResponse> verifyEmail(@RequestParam String token) {
        return ResponseEntity.ok(authService.verifyEmail(token));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<MessageResponse> resentVerification(@Valid @RequestBody EmailRequest  request) {
        return ResponseEntity.ok(authService.resentVerification(request));
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody EmailRequest  request) {
        return ResponseEntity.ok(authService.forgetPassword(request.getEmail()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }


    @PostMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {

        String email = authentication.getName();
        return ResponseEntity.ok(authService.changePassword(request,email));
    }

    @GetMapping("/current-user")
    public ResponseEntity<LoginResponse> currentUser(Authentication authentication) {

        String email = authentication.getName();
        return ResponseEntity.ok(authService.currentUser( email));
    }



}
