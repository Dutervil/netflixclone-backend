package com.wd.netflixcloneback.repository;

import com.wd.netflixcloneback.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
