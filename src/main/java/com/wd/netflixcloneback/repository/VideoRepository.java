package com.wd.netflixcloneback.repository;

import com.wd.netflixcloneback.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {

    @Query("""
    SELECT v FROM Video v WHERE   LOWER(v.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(v.description) LIKE LOWER(CONCAT('%', :search, '%'))
    """)
    Page<Video> searchVideos(@Param("search") String search, Pageable pageable);

    @Query("select  count(v) from Video v where v.published = true ")
    long countPublishedVideos();

    @Query("select  coalesce(sum (v.duration),0) from Video  v")
    long getTotalDuration();

    @Query("""
    SELECT v FROM Video v WHERE v.published =true  AND (
            LOWER(v.title) LIKE LOWER(CONCAT('%', :search, '%')) OR 
            LOWER(v.description) LIKE LOWER(CONCAT('%', :search, '%'))) 
            ORDER BY v.createdAt DESC
       
    """)
    Page<Video> searchPublishedVideos(@Param("search") String search, Pageable pageable);


    @Query("SELECT v FROM Video v WHERE v.published =true ORDER BY v.createdAt DESC")
    Page<Video> findPublishedVideo(Pageable pageable);

    @Query("select v from Video v where v.published = true order by function('RAND') ")
    List<Video> findRandomPublishedVideos(Pageable pageable);
}
