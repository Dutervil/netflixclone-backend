package com.wd.netflixcloneback.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wd.netflixcloneback.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column( nullable = false)
    private String password;

    @Column( nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column( nullable = false)
    private Role role = Role.USER;


    private boolean active=true;
    @Column(nullable = false)

    private  boolean emailVerified=false;

    @Column(unique = true)
    private String verificationToken;
    private Instant verificationTokenExpiry;

    @Column(unique = true)
    private String passwordResetToken;
    private Instant passwordResetTokenExpiry;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_watchlist",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "video_id")
    )
    private Set<Video> videos = new HashSet<>();


    public void addToWatchlist(Video video) {
        videos.add(video);
    }

    public void removeFromWatchlist(Video video) {
        videos.remove(video);
    }


}
