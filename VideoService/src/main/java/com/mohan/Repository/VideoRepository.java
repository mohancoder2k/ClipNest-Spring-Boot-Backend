package com.mohan.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.mohan.Entity.*;
import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
	 List<Video> findByUserOrderByCreatedAtDesc(User user);
}