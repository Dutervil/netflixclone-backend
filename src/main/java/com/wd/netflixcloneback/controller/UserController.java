package com.wd.netflixcloneback.controller;

import com.wd.netflixcloneback.dto.request.UserRequest;
import com.wd.netflixcloneback.dto.response.MessageResponse;
import com.wd.netflixcloneback.dto.response.PageResponse;
import com.wd.netflixcloneback.dto.response.UserResponse;
import com.wd.netflixcloneback.repository.UserRepository;
import com.wd.netflixcloneback.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping()
    public ResponseEntity<MessageResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(userService.createUser(userRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateUser(@Valid @RequestBody UserRequest userRequest, @PathVariable Long id) {
        return ResponseEntity.ok(userService.updateUser(id,userRequest));
    }

    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false ) String search) {
        return ResponseEntity.ok(userService.getUsers(page,size,search));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable Long id, Authentication authentication) {
        String currentUser = authentication.getName();
        return ResponseEntity.ok(userService.deleteUser(id,currentUser));
    }


    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<MessageResponse> toggleStatus(@PathVariable Long id, Authentication authentication) {
        String currentUser = authentication.getName();
        return ResponseEntity.ok(userService.toggleStatus(id,currentUser));
    }


    @PutMapping("/{id}/change-role")
    public ResponseEntity<MessageResponse> changeRole(@PathVariable Long id, @Valid @RequestBody UserRequest userRequest) {

        return ResponseEntity.ok(userService.changeRole(id,userRequest));
    }


}
