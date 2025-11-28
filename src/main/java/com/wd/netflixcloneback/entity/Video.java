package com.wd.netflixcloneback.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "videos")
@Getter
@Setter
@ToString
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;

    @Column(length =4000)
    private String description;

    private Integer year;
    private String rating;
    private Integer duration;

    @Column(name = "src")
    @JsonIgnore
    private String srcUuid;

    @Column(name = "poster")
    @JsonIgnore
    private String posterUuid;

    @Column(nullable = false)
    private boolean published=false;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "video_categories" ,joinColumns = @JoinColumn(name = "video_id"))
    @Column(name = "category")
    private List<String> categories;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    @Transient
    @JsonProperty("isInWatchlist")
    private Boolean isInWatchlist;

    @JsonProperty("scr")
    public String getScr() {
        if (srcUuid != null && !srcUuid.isEmpty()) {
           return getBaseUrl()+ "/api/files/video/" + srcUuid;
        }
        return null;
    }

    @JsonProperty("poster")
    public String getPoster() {
        if (posterUuid != null && !posterUuid.isEmpty()) {
             return getBaseUrl()+ "/api/files/image/" + posterUuid;
        }
        return null;
    }


    public String getBaseUrl(){
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    }


}
