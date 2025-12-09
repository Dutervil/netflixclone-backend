package com.wd.netflixcloneback.service;

import com.wd.netflixcloneback.utils.FIleHandlerUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FIleUploadServiceImpl implements FIleUploadService {

    private Path videoStorageLocation;
    private Path imageStorageLocation;

    @Value("${file.upload.video.dir}")
    private String videoDir;

    @Value("${file.upload.image.dir}")
    private String imageDir;


    @PostConstruct
    public void init() {
        this.videoStorageLocation = Paths.get(videoDir).toAbsolutePath().normalize();
        this. imageStorageLocation = Paths.get(imageDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.videoStorageLocation);
            Files.createDirectories(this.imageStorageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directories", e);
        }
    }

    @Override
    public String storeVideoFile(MultipartFile file) {
        return storeFile(file,videoStorageLocation);
    }

    @Override
    public String storeImageFile(MultipartFile file) {
        return storeFile(file,imageStorageLocation);
    }

    @Override
    public ResponseEntity<Resource> serveVideo(String uuid, String rangeHeader) {

        try {
          Path filePath=FIleHandlerUtil.findByUUid(videoStorageLocation,uuid);
          Resource fileResource=FIleHandlerUtil.createFullResource(filePath);
          String fileName=fileResource.getFilename();
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) contentType = "video/mp4";



            long fileSize=fileResource.contentLength();
          
          if (isFullContentRequest(rangeHeader)){
              return buildFullVideoResponse(fileResource,contentType,fileName,fileSize);
          }
          return buildPartialVideoResponse(filePath,rangeHeader,contentType,fileName, fileSize);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    private boolean isFullContentRequest(String rangeHeader) {
        return rangeHeader == null || rangeHeader.isEmpty();
    }


    private String storeFile(MultipartFile file, Path storeLocation) {
        String fileExtension = FIleHandlerUtil.extractFileExtension(file.getOriginalFilename());
          String uuid = UUID.randomUUID().toString();
          String fileName = uuid+fileExtension;

          try {
              if (file.isEmpty()) {
                  throw new RuntimeException("Failed to store empty file" + fileName);
              }
              Path target = storeLocation.resolve(fileName);
              Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
              return uuid;
          } catch (IOException e) {
              throw new RuntimeException("Failed to store file", e);
          }

    }

    private ResponseEntity<Resource> buildFullVideoResponse(Resource fileResource, String contentType, String fileName, long fileSize) {

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\""+fileName+"\"")
                .header(HttpHeaders.ACCEPT_RANGES,"bytes")
                .header(HttpHeaders.CONTENT_LENGTH,String.valueOf(fileSize))
                .body(fileResource);
    }

    private ResponseEntity<Resource> buildPartialVideoResponse(
            Path filePath,
            String rangeHeader,
            String contentType,
            String fileName,
            long fileSize
    ) throws IOException {

        long[] range = FIleHandlerUtil.parseRangeHeader(rangeHeader, fileSize);
        long start = range[0];
        long end = range[1];

        if (!isValidRange(start, end, fileSize)) {
            return buildRangeNotSatisfiableResponse(fileSize);
        }

        long contentLength = end - start + 1;

        // Read range chunk
        Resource rangeResource = FIleHandlerUtil.createRangeResource(filePath, start, end);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .header(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileSize)
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength))
                .body(rangeResource);
    }


    private boolean isValidRange(long start, long end, long fileSize) {
        return start >= 0 && start <= end && start < fileSize;
    }


    private ResponseEntity<Resource> buildRangeNotSatisfiableResponse(long fileSize) {
        return ResponseEntity.status(416)
                .header(HttpHeaders.CONTENT_RANGE,"bytes */"+ fileSize).build();
    }



    @Override
    public ResponseEntity<Resource> serveImage(String uuid) {

        try {
            Path filePath=FIleHandlerUtil.findByUUid(imageStorageLocation,uuid);
            Resource fileResource=FIleHandlerUtil.createFullResource(filePath);
            String fileName=fileResource.getFilename();
            String contentType = FIleHandlerUtil.detectImageContentType(fileName);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,"inline; filename=\""+fileName+"\"")
                    .body(fileResource);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

    }

}
