package com.wd.netflixcloneback.utils;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class FIleHandlerUtil {

    private FIleHandlerUtil() {
    }

    public static String extractFileExtension(String originalFileName) {
        String fileExtension = "";
        if (originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return fileExtension;
    }

    public static Path findByUUid(Path directory, String uid) throws Exception {
        try (Stream<Path> files = Files.list(directory)) {
            return files
                    .filter(path -> path.getFileName().toString().toLowerCase().startsWith(uid.toLowerCase()))
                    .findFirst()
                    .orElseThrow(() -> new Exception("File not found for UUID: " + uid));
        }
    }


    public static String detectVideoContentType(String fileName) {
        if (fileName == null) return null;

        fileName = fileName.toLowerCase();

        if (fileName.endsWith(".mp4")) return "video/mp4";
        if (fileName.endsWith(".avi")) return "video/x-msvideo";
        if (fileName.endsWith(".mov")) return "video/quicktime";
        if (fileName.endsWith(".wmv")) return "video/x-ms-wmv";
        if (fileName.endsWith(".flv")) return "video/x-flv";
        if (fileName.endsWith(".mkv")) return "video/x-matroska";
        if (fileName.endsWith(".webm")) return "video/webm";
        if (fileName.endsWith(".mpeg") || fileName.endsWith(".mpg")) return "video/mpeg";
        if (fileName.endsWith(".3gp")) return "video/3gpp";
        if (fileName.endsWith(".ogv")) return "video/ogg";

        return "application/octet-stream";
    }

    public static String detectImageContentType(String fileName) {
        if (fileName == null) return null;

        fileName = fileName.toLowerCase();

        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".gif")) return "image/gif";
        if (fileName.endsWith(".bmp")) return "image/bmp";
        if (fileName.endsWith(".webp")) return "image/webp";
        if (fileName.endsWith(".tiff") || fileName.endsWith(".tif")) return "image/tiff";
        if (fileName.endsWith(".svg")) return "image/svg+xml";
        if (fileName.endsWith(".ico")) return "image/x-icon";
        if (fileName.endsWith(".heic")) return "image/heic";
        if (fileName.endsWith(".avif")) return "image/avif";

        // Default fallback
        return "application/octet-stream";
    }

    public static long[] parseRangeHeader(String rangeHeader, long fileLength) {
        String[] ranges = rangeHeader.replace("bytes=", "").split("-");
        long rangeStart = Long.parseLong(ranges[0]);
        long rangeEnd = ranges.length > 1 && !ranges[1].isEmpty() ? Long.parseLong(ranges[1]) : fileLength - 1;
        return new long[]{rangeStart, rangeEnd};
    }


    public static Resource createRangeResource(Path filePath, long rangeStart, long rangeLength) throws IOException {

           RandomAccessFile fileReader = new RandomAccessFile(filePath.toFile(), "r");
           fileReader.seek(rangeStart);
           InputStream partialContentStream = new InputStream() {

               private long totalBytesRead=0;


               @Override
               public int read() throws IOException {
                   if (totalBytesRead >= rangeLength){
                       fileReader.close();
                       return -1;
                   }
                   totalBytesRead++;
                   return fileReader.read();
               }

               @Override
               public int read(byte[] buffer, int offset, int length) throws IOException {
                   if (totalBytesRead >= rangeLength){
                      fileReader.close();
                      return -1;
                   }
                   long remainingBytes = rangeLength - totalBytesRead;
                   int byteToRead = (int) Math.min(length,remainingBytes);
                   int bytesActuallyRead = fileReader.read(buffer, offset, byteToRead);

                   if (bytesActuallyRead >0 ){
                       totalBytesRead += bytesActuallyRead;
                   }

                   if (totalBytesRead >= rangeLength){
                       fileReader.close();
                   }
                    return bytesActuallyRead;
               }
               @Override
               public void close() throws IOException {
                   fileReader.close();
               }
           };


      return new InputStreamResource(partialContentStream){
         @Override
          public long contentLength() {
             return rangeLength;
         }
      };

    }

    public static  Resource createFullResource(Path path) throws IOException {
        Resource resource = new UrlResource(path.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new IOException("Resource not found: " + path);
        }
        return resource;
    }
}
