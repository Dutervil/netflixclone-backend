package com.wd.netflixcloneback.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FIleUploadService {
    String storeVideoFile(MultipartFile file);

    String storeImageFile(MultipartFile file);

    ResponseEntity<Resource> serveVideo(String uuid, String rangeHeader);

    ResponseEntity<Resource> serveImage(String uuid);
}
