package com.example.ConnectUs.repository.postgres;

import com.example.ConnectUs.model.postgres.FriendRequest;
import com.example.ConnectUs.model.postgres.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {
    Optional<FriendRequest> findById(Integer id);
}
