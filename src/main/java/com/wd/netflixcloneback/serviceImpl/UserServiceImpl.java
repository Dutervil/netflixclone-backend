package com.wd.netflixcloneback.serviceImpl;

import com.wd.netflixcloneback.dto.request.UserRequest;
import com.wd.netflixcloneback.dto.response.MessageResponse;
import com.wd.netflixcloneback.dto.response.PageResponse;
import com.wd.netflixcloneback.dto.response.UserResponse;
import com.wd.netflixcloneback.entity.User;
import com.wd.netflixcloneback.enums.Role;
import com.wd.netflixcloneback.exception.EmailAlreadyExistException;
import com.wd.netflixcloneback.exception.InvalidRoleException;
import com.wd.netflixcloneback.repository.UserRepository;
import com.wd.netflixcloneback.security.JwtUtil;
import com.wd.netflixcloneback.service.EmailService;
import com.wd.netflixcloneback.service.UserService;
import com.wd.netflixcloneback.utils.PaginationUtils;
import com.wd.netflixcloneback.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ServiceUtils serviceUtils;
    private final EmailService emailService;

    @Override
    public MessageResponse createUser(UserRequest userRequest) {
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new EmailAlreadyExistException("Email already exist");
        }

        validateRole(userRequest.getRole());
        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setFullName(userRequest.getFullName());
        user.setRole(Role.valueOf( userRequest.getRole()));
        user.setActive(true);
        user.setEmailVerified(true);
        userRepository.save(user);
        return new MessageResponse("account created");
    }

    @Override
    public MessageResponse updateUser(Long id, UserRequest userRequest) {
        User user=serviceUtils.getUserByIdOrThrow(id);
        ensureNotLastActiveAdmin(user);
        validateRole(userRequest.getRole());
        user.setFullName(userRequest.getFullName());
        user.setRole(Role.valueOf( userRequest.getRole().toUpperCase()));
        userRepository.save(user);
        return new MessageResponse("account updated");
    }

    @Override
    public PageResponse<UserResponse> getUsers(int page, int size, String search) {
        Pageable pageable = PaginationUtils.createPageRequest(page, size,"id");
        Page<User> userPage ;
        if (search != null && !search.isEmpty()) {
            userPage=userRepository.searchUsers(search.trim(),pageable);
        }else {
            userPage=userRepository.findAll(pageable);
        }
        return PaginationUtils.toPageResponse(userPage,UserResponse::fromEntity);
    }

    @Override
    public MessageResponse deleteUser(Long id, String currentUser) {
        User user=serviceUtils.getUserByIdOrThrow(id);
        if (currentUser.equals(user.getEmail())) {
            throw new RuntimeException("You cannot delete your own account");
        }
        ensureNotLastAdmin(user,"delete user");
        userRepository.deleteById(id);
        return new MessageResponse("User deleted successfully");
    }

    @Override
    public MessageResponse toggleStatus(Long id, String currentUser) {
        User user=serviceUtils.getUserByIdOrThrow(id);
        if (currentUser.equals(user.getEmail())) {
            throw new RuntimeException("You cannot deactivate your own account");
        }
        ensureNotLastActiveAdmin(user);
        user.setActive(!user.isActive());
        userRepository.save(user);
        return new MessageResponse("user is now "+(user.isActive()? "active" : "inactive"));
    }

    @Override
    public MessageResponse changeRole(Long id, UserRequest userRequest) {
         User user=serviceUtils.getUserByIdOrThrow(id);
         validateRole(userRequest.getRole());
         Role newRole=Role.valueOf( userRequest.getRole().toUpperCase());

         if (user.getRole() == Role.ADMIN && newRole == Role.USER){
             ensureNotLastAdmin(user,"change role");
         }
         user.setRole(newRole);
         userRepository.save(user);
        return new  MessageResponse("User role updated");
    }

    private void ensureNotLastAdmin(User user,String action) {
        if (user.getRole() == Role.ADMIN) {
            long adminCount = userRepository.countByRole(Role.ADMIN);
            if (adminCount <=1 ) {
                throw new InvalidRoleException("Cannot "+action+"  the last admin");
            }
        }
    }

    private void ensureNotLastActiveAdmin(User user) {
        if (user.isActive() && user.getRole() == Role.ADMIN) {
            long activeAdminCount=userRepository.countByRoleAndActive(Role.ADMIN,true);
            if (activeAdminCount <=1) {
                throw new RuntimeException("Cannot deactivate the last active user");
            }
        }
    }

    private void validateRole(String role) {
        log.info("Validating role {}", role);
        try {
            Role.valueOf(role.toUpperCase());
        } catch (InvalidRoleException ex) {
            String allowed = Arrays.stream(Role.values()).map(Enum::name).collect(Collectors.joining(", "));
            throw new InvalidRoleException(
                    "Invalid role: " + role + ". Allowed values are: " + allowed
            );
        }
    }

}
