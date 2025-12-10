package com.wd.netflixcloneback.controller;

import com.wd.netflixcloneback.dto.request.VideoRequest;
import com.wd.netflixcloneback.dto.response.MessageResponse;
import com.wd.netflixcloneback.dto.response.PageResponse;
import com.wd.netflixcloneback.dto.response.VideoResponse;
import com.wd.netflixcloneback.dto.response.VideoStatsResponse;
import com.wd.netflixcloneback.service.VideoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<MessageResponse> createVideoByAdmin(@Valid @RequestBody VideoRequest videoRequest) {
        return ResponseEntity.ok(videoService.createVideoByAdmin(videoRequest));

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<PageResponse<VideoResponse>> getAllAdminVideos( @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false ) String search) {
        return ResponseEntity.ok(videoService.getAllAdminVideos(page,size,search));

    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{id}")
    public ResponseEntity<MessageResponse> updateVideoByAdmin(@Valid @RequestBody VideoRequest videoRequest, @PathVariable("id") Long id) {
        return ResponseEntity.ok(videoService.updateVideoByAdmin(videoRequest,id));

    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<MessageResponse> deleteVideoByAdmin( @PathVariable("id") Long id) {
        return ResponseEntity.ok(videoService.deleteVideoByAdmin(id));

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/{id}/publish")
    public ResponseEntity<MessageResponse> toggleVideoPublishStatusByAdmin( @PathVariable("id") Long id, @RequestParam boolean value) {
        return ResponseEntity.ok(videoService.toggleVideoPublishStatusByAdmin(id,value));

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/stats")
    public ResponseEntity<VideoStatsResponse> getAdminStats() {
        return ResponseEntity.ok(videoService.getAdminStats());

    }

    @GetMapping("/published")
    public ResponseEntity<PageResponse<VideoResponse>> getPublishedVideos(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
                                                                          @RequestParam(required = false ) String search, Authentication authentication) {
        return ResponseEntity.ok(videoService.getPublishedVideos(page,size,search,authentication.getName()));

    }

    @GetMapping("/featured")
    public ResponseEntity<List<VideoResponse>> getFeaturedVideos() {
        return ResponseEntity.ok(videoService.getFeaturedVideos());

    }
}
