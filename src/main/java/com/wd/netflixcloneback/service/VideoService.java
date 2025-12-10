package com.wd.netflixcloneback.service;

import com.wd.netflixcloneback.dto.request.VideoRequest;
import com.wd.netflixcloneback.dto.response.MessageResponse;
import com.wd.netflixcloneback.dto.response.PageResponse;
import com.wd.netflixcloneback.dto.response.VideoResponse;
import com.wd.netflixcloneback.dto.response.VideoStatsResponse;
import org.jspecify.annotations.Nullable;

import java.util.List;


public interface VideoService {
      MessageResponse createVideoByAdmin(  VideoRequest videoRequest);

       PageResponse<VideoResponse> getAllAdminVideos(int page, int size, String search);

       MessageResponse updateVideoByAdmin( VideoRequest videoRequest, Long id);

       MessageResponse deleteVideoByAdmin(Long id);

       MessageResponse toggleVideoPublishStatusByAdmin(Long id, boolean value);

      VideoStatsResponse getAdminStats();

       PageResponse<VideoResponse> getPublishedVideos(int page, int size, String search, String name);

      List<VideoResponse> getFeaturedVideos();
}
