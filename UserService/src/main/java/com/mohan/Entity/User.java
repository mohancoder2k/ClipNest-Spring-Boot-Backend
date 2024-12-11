package com.mohan.Entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Document(collection = "users") // MongoDB annotation for collections
public class User {
    @Id
    private String id;

    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
   
    private byte[] profilePhoto; // Profile photo stored as byte array
    private LocalDateTime createdAt = LocalDateTime.now(); // Timestamp of when the user is created
    private Set<Role> roles = new HashSet<>(); // User roles (if required)

    // Method to retrieve role names
    public Set<String> getRoleNames() {
        if (roles == null || roles.isEmpty()) { // Check for null or empty set
            return new HashSet<>();
        }
        return roles.stream().map(Role::getName).collect(Collectors.toSet());
    }
}
