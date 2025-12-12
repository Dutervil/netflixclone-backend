package com.wd.netflixcloneback.service;

import com.wd.netflixcloneback.dto.response.MessageResponse;
import com.wd.netflixcloneback.dto.response.PageResponse;
import com.wd.netflixcloneback.dto.response.VideoResponse;
import org.jspecify.annotations.Nullable;

public interface WatchListService {
    MessageResponse addWatchList(String name, Long videoId);
     MessageResponse removeFromWatchList(String name, Long videoId);

     PageResponse<VideoResponse> getWatchListPaginated(String name, int page, int size, String search);
}
