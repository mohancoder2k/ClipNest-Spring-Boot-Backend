package com.mohan.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mohan.Entity.Video;
import com.mohan.Service.UserService;
import com.mohan.Service.VideoService;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000") 
@RestController
@RequestMapping("/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private UserService userService; // Use user service to fetch user information

    @PostMapping("/upload")
    public ResponseEntity<?> uploadVideo(
            Authentication authentication,
            @RequestParam("title") String title,
            @RequestParam("videoFile") MultipartFile videoFile) {

        // Find user ID based on the authenticated username
        Long userId = userService.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found")).getId();

        try {
            // Upload the video using the VideoService
            Video video = videoService.uploadVideo(userId, title, videoFile);
            return ResponseEntity.ok(video);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to upload video: " + e.getMessage());
        }
    }


    @GetMapping("/user")
    public ResponseEntity<?> getUserVideos(Authentication authentication) {
        try {
            Long userId = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found")).getId();

            List<Video> videos = videoService.getUserVideos(userId);
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to fetch videos: " + e.getMessage());
        }
    }
    @GetMapping("/stream/{videoId}")
    public ResponseEntity<?> streamVideo(@PathVariable Long videoId) {
        try {
            Video video = videoService.getVideoById(videoId)
                    .orElseThrow(() -> new RuntimeException("Video not found"));

            byte[] videoData = video.getVideoData();
            return ResponseEntity.ok()
                    .header("Content-Type", "video/mp4")  // Assuming mp4 format, modify if needed
                    .header("Content-Length", String.valueOf(videoData.length))
                    .body(videoData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to stream video: " + e.getMessage());
        }
    }

}