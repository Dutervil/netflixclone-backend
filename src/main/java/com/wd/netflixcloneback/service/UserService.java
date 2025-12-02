package com.wd.netflixcloneback.service;

import com.wd.netflixcloneback.dto.request.UserRequest;
import com.wd.netflixcloneback.dto.response.MessageResponse;
import com.wd.netflixcloneback.dto.response.PageResponse;
import com.wd.netflixcloneback.dto.response.UserResponse;
import org.jspecify.annotations.Nullable;


public interface UserService {
    MessageResponse createUser( UserRequest userRequest);

     MessageResponse updateUser(Long id,   UserRequest userRequest);

     PageResponse<UserResponse> getUsers(int page, int size, String search);

     MessageResponse deleteUser(Long id, String currentUser);

     MessageResponse toggleStatus(Long id, String currentUser);

     MessageResponse changeRole(Long id, UserRequest userRequest);
}
