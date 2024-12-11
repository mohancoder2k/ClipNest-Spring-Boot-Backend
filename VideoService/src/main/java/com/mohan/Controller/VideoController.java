package com.mohan.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mohan.Entity.Video;
import com.mohan.Service.UserService;
import com.mohan.Service.VideoService;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "http://192.168.1.7:3000", allowCredentials = "true")
@RestController
@RequestMapping("/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private UserService userService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadVideo(
            Authentication authentication,
            @RequestParam("title") String title,
            @RequestParam("videoFile") MultipartFile videoFile) {

        System.out.println("Video upload started...");

        // Validate video file type
        if (!videoFile.getContentType().startsWith("video/")) {
            System.out.println("Invalid file type uploaded: " + videoFile.getContentType());
            return ResponseEntity.badRequest().body("Invalid file type. Please upload a video file.");
        }

        // Find user ID based on the authenticated username
        String userId = userService.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found")).getId();
        System.out.println("User found with ID: " + userId);

        try {
            // Upload the video using the VideoService
            Video video = videoService.uploadVideo(userId, title, videoFile);
            System.out.println("Video uploaded successfully with ID: " + video.getId());
            return ResponseEntity.ok(video);
        } catch (IOException e) {
            System.out.println("Error uploading video: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to upload video: " + e.getMessage());
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserVideos(Authentication authentication) {
        System.out.println("Fetching user videos...");

        try {
            String userId = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found")).getId();
            System.out.println("User found with ID: " + userId);

            List<Video> videos = videoService.getUserVideos(userId);
            System.out.println("Found " + videos.size() + " videos for user with ID: " + userId);
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            System.out.println("Error fetching user videos: " + e.getMessage());
            return ResponseEntity.status(500).body("Failed to fetch videos: " + e.getMessage());
        }
    }

    @GetMapping("/stream/{videoId}")
    public ResponseEntity<?> streamVideo(@PathVariable String videoId) {
        System.out.println("Streaming video with ID: " + videoId);

        try {
            Video video = videoService.getVideoById(videoId)
                    .orElseThrow(() -> new RuntimeException("Video not found"));
            System.out.println("Video found for streaming: " + video.getId());

            byte[] videoData = video.getVideoData();
            String contentType = "video/mp4";  // Default to mp4 if not detected
            System.out.println("Video stream ready with content type: " + contentType);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(videoData.length))
                    .body(videoData);
        } catch (Exception e) {
            System.out.println("Error streaming video: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to stream video", e.getMessage()));
        }
    }

    public static class ErrorResponse {
        private String message;
        private String details;

        // Constructor, Getters and Setters
        public ErrorResponse(String message, String details) {
            this.message = message;
            this.details = details;
        }
        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
        public String getDetails() {
            return details;
        }
        public void setDetails(String details) {
            this.details = details;
        }
    }
}
