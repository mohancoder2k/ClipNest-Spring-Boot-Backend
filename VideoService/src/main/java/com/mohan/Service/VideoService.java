package com.mohan.Service;

import com.mohan.Entity.User; // Correct User import
import com.mohan.Entity.Video;
import com.mohan.Repository.UserRepository;
import com.mohan.Repository.VideoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserRepository userRepository; // Add user repository to fetch user

    public Video uploadVideo(Long userId, String title, MultipartFile videoFile) throws IOException {
        // Fetch the user from the UserRepository
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        
        // Create a new Video entity
        Video video = new Video();
        video.setTitle(title);
        video.setUser(user); // Set the user to the video object
        video.setVideoData(videoFile.getBytes()); // Set the video bytes
        return videoRepository.save(video); // Save and return the video
    }
    public Optional<Video> getVideoById(Long videoId) {
        return videoRepository.findById(videoId);
    }

    // Fetch all videos uploaded by a specific user
    public List<Video> getUserVideos(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return videoRepository.findByUserOrderByCreatedAtDesc(user); // Fetch sorted videos by the user
    }
}
