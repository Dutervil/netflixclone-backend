package com.wd.netflixcloneback.service;

import com.wd.netflixcloneback.dto.request.ChangePasswordRequest;
import com.wd.netflixcloneback.dto.request.EmailRequest;
import com.wd.netflixcloneback.dto.request.ResetPasswordRequest;
import com.wd.netflixcloneback.dto.request.UserRequest;
import com.wd.netflixcloneback.dto.response.EmailValidationResponse;
import com.wd.netflixcloneback.dto.response.LoginResponse;
import com.wd.netflixcloneback.dto.response.MessageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.Nullable;


public interface AuthService {

     MessageResponse signup( UserRequest userRequest);

      LoginResponse login(  String email,  String password);


      EmailValidationResponse validateEmail(String token);

      MessageResponse verifyEmail(String token);

      MessageResponse resentVerification(  EmailRequest request);

      MessageResponse forgetPassword( String email);

      MessageResponse resetPassword( ResetPasswordRequest request);

      MessageResponse changePassword( ChangePasswordRequest request, String email);
      LoginResponse currentUser(String email);
}
