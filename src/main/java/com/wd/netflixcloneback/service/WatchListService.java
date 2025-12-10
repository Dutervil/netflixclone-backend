package com.wd.netflixcloneback.service;

import com.wd.netflixcloneback.dto.response.MessageResponse;
import org.jspecify.annotations.Nullable;

public interface WatchListService {
    MessageResponse addWatchList(String name, Long videoId);
     MessageResponse removeFromWatchList(String name, Long videoId);
}
