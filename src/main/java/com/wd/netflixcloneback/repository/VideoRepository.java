package com.wd.netflixcloneback.repository;

import com.wd.netflixcloneback.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
}
