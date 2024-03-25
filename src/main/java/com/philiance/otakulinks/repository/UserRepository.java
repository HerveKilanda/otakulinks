package com.philiance.otakulinks.repository;

import com.philiance.otakulinks.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findOneByEmail(String email);
}
