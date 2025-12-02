package com.wd.netflixcloneback.utils;

import com.wd.netflixcloneback.entity.User;
import com.wd.netflixcloneback.entity.Video;
import com.wd.netflixcloneback.exception.ResourceNotFoundException;
import com.wd.netflixcloneback.repository.UserRepository;
import com.wd.netflixcloneback.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceUtils {

    private final UserRepository userRepository;
    private final VideoRepository videoRepository;


    public User getUserByEmailOrThrow(String email){
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public  User getUserByIdOrThrow(Long id){
        return userRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + id));
    }

    public Video getVideoByIdOrThrow(Long id){
        return videoRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + id));
    }
}
