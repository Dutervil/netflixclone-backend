package com.wd.netflixcloneback.controller;

import com.wd.netflixcloneback.service.FIleUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {
    private   final FIleUploadService fIleUploadService;


    @PostMapping("/upload/video")
    public ResponseEntity<Map<String, String>> uploadVideo(@RequestParam("file") MultipartFile file) {
        String uuid= fIleUploadService.storeVideoFile(file);
        return ResponseEntity.ok().body(buildUploadResponse(uuid,file));

    }

    @PostMapping("/upload/image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String uuid= fIleUploadService.storeImageFile(file);
        return ResponseEntity.ok().body(buildUploadResponse(uuid,file));

    }

    @GetMapping("/video/{uuid}")
    public ResponseEntity<Resource> serveVideo(
            @PathVariable String uuid,
            @RequestHeader(value = HttpHeaders.RANGE,required = false) String rangeHeader,
            @RequestParam(value = "token",required = false) String tokenParam) {
        if (rangeHeader != null && !rangeHeader.startsWith("bytes=")) {
            rangeHeader = "bytes=" + rangeHeader;
        }
        return fIleUploadService.serveVideo(uuid,rangeHeader);

    }

    @GetMapping("/image/{uuid}")
    public ResponseEntity<Resource> serveImage(@PathVariable String uuid) {
        return fIleUploadService.serveImage(uuid);

    }




    private Map<String, String> buildUploadResponse(String uuid, MultipartFile file) {
        Map<String, String> uploadResponse = new HashMap<>();
        uploadResponse.put("uuid", uuid);
        uploadResponse.put("filename", file.getOriginalFilename());
        uploadResponse.put("size", String.valueOf(file.getSize()));
        return uploadResponse;
    }

}
