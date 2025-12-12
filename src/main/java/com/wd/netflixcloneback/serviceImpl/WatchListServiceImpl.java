package com.wd.netflixcloneback.serviceImpl;

import com.wd.netflixcloneback.dto.response.MessageResponse;
import com.wd.netflixcloneback.dto.response.PageResponse;
import com.wd.netflixcloneback.dto.response.VideoResponse;
import com.wd.netflixcloneback.entity.User;
import com.wd.netflixcloneback.entity.Video;
import com.wd.netflixcloneback.repository.UserRepository;
import com.wd.netflixcloneback.repository.VideoRepository;
import com.wd.netflixcloneback.service.WatchListService;
import com.wd.netflixcloneback.utils.PaginationUtils;
import com.wd.netflixcloneback.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WatchListServiceImpl implements WatchListService {
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final ServiceUtils serviceUtils;



    @Override
    public MessageResponse addWatchList(String email, Long videoId) {

        User user=serviceUtils.getUserByEmailOrThrow(email);
        Video video=serviceUtils.getVideoByIdOrThrow(videoId);
        user.addToWatchlist(video);
        userRepository.save(user);
        return new MessageResponse("Successfully added watchlist");
    }

    @Override
    public MessageResponse removeFromWatchList(String email, Long videoId) {
        User user=serviceUtils.getUserByEmailOrThrow(email);
        Video video=serviceUtils.getVideoByIdOrThrow(videoId);
        user.removeFromWatchlist(video);
        userRepository.save(user);
        return new MessageResponse("Successfully remove from watchlist");
    }

    @Override
    public PageResponse<VideoResponse> getWatchListPaginated(String email, int page, int size, String search) {

        User user=serviceUtils.getUserByEmailOrThrow(email);
        Pageable pageable= PaginationUtils.createPageRequest(page, size);
        Page<Video> videoPage;

        if (search != null && !search.isEmpty()) {
            videoPage=userRepository.searchWatchListByUserId(user.getId(),search.trim(),pageable);
        }else{
            videoPage=userRepository.findWatchListByUserId(user.getId(),pageable);
        }

        return PaginationUtils.toPageResponse(videoPage,VideoResponse::fromEntity);
    }
}
