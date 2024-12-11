package com.mohan.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import com.mohan.Entity.*;
import com.mohan.Repository.*;
import com.mohan.Service.*;
import com.mohan.JWT.*;

@CrossOrigin(origins = "http://192.168.1.7:3000", allowCredentials = "true")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping(value = "/signup", consumes = {"multipart/form-data"})
    public ResponseEntity<?> signup(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("profilePhoto") MultipartFile profilePhoto) {

        byte[] profilePhotoBytes = null;
        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            try {
                profilePhotoBytes = profilePhoto.getBytes(); // Get bytes from the MultipartFile
            } catch (IOException e) {
                return ResponseEntity.badRequest().body("Failed to upload profile photo: " + e.getMessage());
            }
        } else {
            return ResponseEntity.badRequest().body("Profile photo is required");
        }

        Optional<User> newUser = userService.registerUser(username, email, password, firstName, lastName, profilePhotoBytes);
        if (newUser.isPresent()) {
            return ResponseEntity.ok("User registered successfully");
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Username or Email already exists"));
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication) {
        String username = authentication.getName(); // Get the username from authentication
        Optional<User> user = userService.findByUsername(username);

        if (user.isPresent()) {
            User foundUser = user.get();
            String base64ProfilePhoto = foundUser.getProfilePhoto() != null
                ? Base64.getEncoder().encodeToString(foundUser.getProfilePhoto())
                : null;

            // Return user details along with the profile photo
            return ResponseEntity.ok(Map.of(
                "username", foundUser.getUsername(),
                "firstName", foundUser.getFirstName(),
                "lastName", foundUser.getLastName(),
                "email", foundUser.getEmail(),
                "profilePhoto", base64ProfilePhoto
            ));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }
    
    @GetMapping("/searchUser")
    public ResponseEntity<?> searchUserByUsername(@RequestParam("username") String username) {
        Optional<User> user = userService.findByUsername(username);
        if (user.isPresent()) {
            User foundUser = user.get();
            String base64ProfilePhoto = foundUser.getProfilePhoto() != null
                ? Base64.getEncoder().encodeToString(foundUser.getProfilePhoto())
                : null;

            // Return username and profile photo in response
            return ResponseEntity.ok(Map.of(
                "username", foundUser.getUsername(),
                "profilePhoto", base64ProfilePhoto
            ));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }


    @PutMapping("/user")
    public ResponseEntity<?> updateUser(
    		 Authentication authentication,
    	        @RequestParam("email") String email,
    	        @RequestParam("firstName") String firstName,
    	        @RequestParam("lastName") String lastName,
    	        @RequestParam(value = "profilePhoto", required = false) MultipartFile profilePhoto) {

        String username = authentication.getName();
        byte[] profilePhotoBytes = null;

        // If profile photo is provided, convert it to bytes
        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            try {
                profilePhotoBytes = profilePhoto.getBytes();
            } catch (IOException e) {
                return ResponseEntity.badRequest().body("Failed to upload profile photo: " + e.getMessage());
            }
        }

        // Call the service to update the user details
        Optional<User> updatedUser = userService.updateUser(username, email, firstName, lastName, profilePhotoBytes);
        if (updatedUser.isPresent()) {
            return ResponseEntity.ok("User updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }


    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUser(Authentication authentication) {
        String username = authentication.getName();
        boolean isDeleted = userService.deleteUser(username);
        if (isDeleted) {
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }




    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> user) {
        Optional<User> loggedInUser = userService.loginUser(user.get("username"), user.get("password"));
        if (loggedInUser.isPresent()) {
            String token = jwtUtil.generateToken(loggedInUser.get().getUsername());
            System.out.println("Generated Token: " + token);
            return ResponseEntity.ok(Map.of("token", token));
        } else {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

}