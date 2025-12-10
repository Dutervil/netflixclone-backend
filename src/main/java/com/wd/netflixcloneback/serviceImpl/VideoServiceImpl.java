package com.wd.netflixcloneback.serviceImpl;

import com.wd.netflixcloneback.dto.request.VideoRequest;
import com.wd.netflixcloneback.dto.response.MessageResponse;
import com.wd.netflixcloneback.dto.response.PageResponse;
import com.wd.netflixcloneback.dto.response.VideoResponse;
import com.wd.netflixcloneback.dto.response.VideoStatsResponse;
import com.wd.netflixcloneback.entity.Video;
import com.wd.netflixcloneback.repository.UserRepository;
import com.wd.netflixcloneback.repository.VideoRepository;
import com.wd.netflixcloneback.service.VideoService;
import com.wd.netflixcloneback.utils.PaginationUtils;
import com.wd.netflixcloneback.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
 import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final ServiceUtils serviceUtils;


    @Override
    public MessageResponse createVideoByAdmin(VideoRequest videoRequest) {
        Video video = new Video();
        buildVideo(videoRequest, video);
        videoRepository.save(video);
        return new MessageResponse("Video created successfully");
    }
    @Override
    public MessageResponse updateVideoByAdmin(VideoRequest videoRequest, Long id) {

        Video video = new Video();
        video.setId(id);
        buildVideo(videoRequest, video);
        videoRepository.save(video);
        return new MessageResponse("Video updated successfully");
    }

    @Override
    public MessageResponse deleteVideoByAdmin(Long id) {
        if (!videoRepository.existsById(id)) {
            throw new IllegalArgumentException("Video not found");
        }
        videoRepository.deleteById(id);
        return new MessageResponse("Video deleted successfully");
    }

    @Override
    public MessageResponse toggleVideoPublishStatusByAdmin(Long id, boolean status) {
        Video video = videoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Video not found"));
        video.setPublished(status);
        videoRepository.save(video);
        return new MessageResponse(" the video published status is " + status);
    }

    @Override
    public VideoStatsResponse getAdminStats() {
        long total = videoRepository.count();
        long publishedVideos=videoRepository.countPublishedVideos();
        long totalDuration = videoRepository.getTotalDuration();
        return new VideoStatsResponse(total, publishedVideos, totalDuration);
    }

    @Override
    public PageResponse<VideoResponse> getPublishedVideos(int page, int size, String search, String email) {
        Pageable pageable = PaginationUtils.createPageRequest(page, size,"id");
        Page<Video> videos;
        if (search != null && !search.isEmpty()) {
            videos=videoRepository.searchPublishedVideos(search.trim(),pageable);
        }else{
            videos=videoRepository.findPublishedVideo(pageable);
        }
        List<Video> videosList = videos.getContent();
        Set<Long> watchListIds = Set.of();
        if (!videosList.isEmpty()) {
            try {
             List<Long> videoIds = videos.getContent().stream().map(Video::getId).toList();
             watchListIds=userRepository.findWatchListVideoIds(email,videoIds);
            } catch (Exception e) {
                watchListIds = Set.of();
            }
        }
        Set<Long> finalWatchListIds = watchListIds;
        videosList.forEach(video -> video.setIsInWatchlist(finalWatchListIds.contains(video.getId())));
        List<VideoResponse> videoResponses=videosList.stream().map(VideoResponse::fromEntity).toList();
        return PaginationUtils.toPageResponse(videos,videoResponses);
    }

    @Override
    public List<VideoResponse> getFeaturedVideos() {
        Pageable pageable= PageRequest.of(0, 5);
        List<Video> videos=videoRepository.findRandomPublishedVideos(pageable);
        return videos.stream().map(VideoResponse::fromEntity).toList();
    }


    @Override
    public PageResponse<VideoResponse> getAllAdminVideos(int page, int size, String search) {

        Pageable pageable = PaginationUtils.createPageRequest(page, size,"id");
        Page<Video> videos;
        if (search != null && !search.isEmpty()) {
            videos=videoRepository.searchVideos(search.trim(),pageable);
        }else{
            videos=videoRepository.findAll(pageable);
        }
        return PaginationUtils.toPageResponse(videos,VideoResponse::fromEntity);
    }

    private void buildVideo(VideoRequest videoRequest, Video video) {
        video.setTitle(videoRequest.getTitle());
        video.setDescription(videoRequest.getDescription());
        video.setYear(videoRequest.getYear());
        video.setRating(videoRequest.getRating());
        video.setDuration(videoRequest.getDuration());
        video.setSrcUuid(videoRequest.getSrc());
        video.setPosterUuid(videoRequest.getPoster());
        video.setPublished(videoRequest.isPublished());
        video.setCategories(videoRequest.getCategories()!=null?videoRequest.getCategories(): List.of());
    }

}
