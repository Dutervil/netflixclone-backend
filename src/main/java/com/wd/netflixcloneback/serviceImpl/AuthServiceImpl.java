package com.wd.netflixcloneback.serviceImpl;

import com.wd.netflixcloneback.dto.request.ChangePasswordRequest;
import com.wd.netflixcloneback.dto.request.EmailRequest;
import com.wd.netflixcloneback.dto.request.ResetPasswordRequest;
import com.wd.netflixcloneback.dto.request.UserRequest;
import com.wd.netflixcloneback.dto.response.EmailValidationResponse;
import com.wd.netflixcloneback.dto.response.LoginResponse;
import com.wd.netflixcloneback.dto.response.MessageResponse;
import com.wd.netflixcloneback.entity.User;
import com.wd.netflixcloneback.enums.Role;
import com.wd.netflixcloneback.exception.*;
import com.wd.netflixcloneback.repository.UserRepository;
import com.wd.netflixcloneback.security.JwtUtil;
import com.wd.netflixcloneback.service.AuthService;
import com.wd.netflixcloneback.service.EmailService;
import com.wd.netflixcloneback.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final ServiceUtils serviceUtils;

    @Override
    public MessageResponse signup(UserRequest userRequest) {

        if (this.userRepository.existsByEmail(userRequest.getEmail())) {
            throw new EmailAlreadyExistException("Email already exists");
        }
        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setFullName(userRequest.getFullName());
        user.setRole(Role.USER);
        user.setEmailVerified(false);
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(Instant.now().plusSeconds(3600)); //1HOUR
        userRepository.save(user);
        emailService.sendVerificationEmail(userRequest.getEmail(), verificationToken);
        return new MessageResponse("Registration Successful. Please check your email to verify your account");
    }

    @Override
    public LoginResponse login(String email, String password) {

        User user = userRepository
                .findByEmail(email)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid Email or Password"));
        if (!user.isActive()) {
            throw new AccountDeactivatedException("Your account is deactivated. Please contact your administrator for assistance.");
        }
        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException("Please verify your email to verify your account");
        }
        final String token = jwtUtil.generateToken(user.getEmail(), user.getRole().toString());

        return new LoginResponse(
                token,
                user.getEmail(),
                user.getFullName(),
                user.getRole().name()
        );
    }

    @Override
    public EmailValidationResponse validateEmail(String email) {
        boolean exists = userRepository.existsByEmail(email);
        return new EmailValidationResponse(exists, !exists);
    }

    @Override
    public MessageResponse verifyEmail(String token) {
        User user = userRepository
                .findByVerificationToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired Verification Token"));
        if (user.getVerificationTokenExpiry() == null || user.getVerificationTokenExpiry().isBefore(Instant.now())) {
            throw new InvalidTokenException(" Verification link has expired. please request a new one");
        }
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
        return new MessageResponse("Your account has been verified successfully. You can now login");
    }

    @Override
    public MessageResponse resentVerification(EmailRequest request) {
        User user=serviceUtils.getUserByEmailOrThrow(request.getEmail());

        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(Instant.now().plusSeconds(3600)); //1HOUR
        userRepository.save(user);
        emailService.sendVerificationEmail(request.getEmail(), verificationToken);
        return new MessageResponse("New Verification Link has been sent to your email.");

    }

    @Override
    public MessageResponse forgetPassword(String email) {
        User user=serviceUtils.getUserByEmailOrThrow(email);
        String verificationToken = UUID.randomUUID().toString();
        user.setPasswordResetToken(verificationToken);
        user.setPasswordResetTokenExpiry(Instant.now().plusSeconds(3600)); //1HOUR
        userRepository.save(user);
        emailService.sendPasswordResetEmail(user.getEmail(), verificationToken);
        return new MessageResponse("A link has been sent to your email to reset your password");
    }

    @Override
    public MessageResponse resetPassword(ResetPasswordRequest request) {

        User user = userRepository
                .findByPasswordResetToken(request.getToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired password reset Token"));
        if (user.getPasswordResetTokenExpiry() == null || user.getPasswordResetTokenExpiry().isBefore(Instant.now())) {
            throw new InvalidTokenException(" Reset password token link has expired. please request a new one");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);
        return new MessageResponse("Your password has been reset successfully");
    }

    @Override
    public MessageResponse changePassword(ChangePasswordRequest request, String email) {

        User user=serviceUtils.getUserByEmailOrThrow(email);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Current password is not correct");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return new MessageResponse("Your password has been changed successfully");
    }

    @Override
    public LoginResponse currentUser(String email) {
        User user=serviceUtils.getUserByEmailOrThrow(email);
        return new LoginResponse(
                null,
                user.getEmail(),
                user.getFullName(),
                user.getRole().name()
        );

    }
}
