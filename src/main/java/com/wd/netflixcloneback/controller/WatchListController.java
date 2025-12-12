package com.wd.netflixcloneback.controller;

import com.wd.netflixcloneback.dto.response.MessageResponse;
import com.wd.netflixcloneback.dto.response.PageResponse;
import com.wd.netflixcloneback.dto.response.VideoResponse;
import com.wd.netflixcloneback.service.WatchListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
public class WatchListController {
    private final WatchListService watchListService;


    @PostMapping("/{videoId}")
    public ResponseEntity<MessageResponse> addWatchList(@PathVariable("videoId") Long videoId, Authentication auth) {
        return ResponseEntity.ok(watchListService.addWatchList(auth.getName(),videoId));

    }

    @DeleteMapping("/{videoId}")
    public ResponseEntity<MessageResponse> removeFromWatchList(@PathVariable("videoId") Long videoId, Authentication auth) {
        return ResponseEntity.ok(watchListService.removeFromWatchList(auth.getName(),videoId));

    }

    @GetMapping()
    public ResponseEntity<PageResponse<VideoResponse>> getWatchList(@RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size,
                                                                    @RequestParam(required = false ) String search,
                                                                    Authentication auth) {

        return ResponseEntity.ok(watchListService.getWatchListPaginated(auth.getName(),page,size,search));

    }
}
