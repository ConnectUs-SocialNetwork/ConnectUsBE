package com.example.ConnectUs.repository.postgres;

import com.example.ConnectUs.model.postgres.FriendRequest;
import com.example.ConnectUs.model.postgres.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {
    Optional<FriendRequest> findById(Integer id);

    @Query("SELECT COUNT(fr) > 0 FROM FriendRequest fr " +
            "WHERE fr.user.id = :myId " +
            "AND fr.friend.id = :userId " +
            "AND fr.status = 0")
    boolean hasPendingFriendRequest(@Param("myId") Integer myId, @Param("userId") Integer userId);
}
