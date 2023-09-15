package com.example.ConnectUs.repository.postgres;

import java.util.Optional;

import com.example.ConnectUs.model.postgres.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

}
