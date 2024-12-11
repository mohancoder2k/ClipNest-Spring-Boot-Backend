package com.mohan.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.mohan.Entity.Video;
import com.mohan.Entity.User;

import java.util.List;

@Repository
public interface VideoRepository extends MongoRepository<Video, String> {
    List<Video> findByUserOrderByCreatedAtDesc(User user);
}
