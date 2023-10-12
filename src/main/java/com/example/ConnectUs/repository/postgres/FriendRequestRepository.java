package com.example.ConnectUs.repository.postgres;

import com.example.ConnectUs.model.postgres.FriendRequest;
import com.example.ConnectUs.model.postgres.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {
    Optional<FriendRequest> findById(Integer id);

    @Query("SELECT COUNT(fr) > 0 FROM FriendRequest fr " +
            "WHERE fr.user.id = :myId " +
            "AND fr.friend.id = :userId " +
            "AND fr.status = 0")
    boolean hasPendingFriendRequest(@Param("myId") Integer myId, @Param("userId") Integer userId);

    @Modifying
    @Query("DELETE FROM FriendRequest fr " +
            "WHERE (fr.user.id = :userId AND fr.friend.id = :friendId) OR " +
            "(fr.user.id = :friendId AND fr.friend.id = :userId)")
    @Transactional
    void deleteByUserIdAndFriendId(@Param("userId") Integer userId, @Param("friendId") Integer friendId);

    @Query("SELECT fr FROM FriendRequest fr " +
            "WHERE (fr.user.id = :userId AND fr.friend.id = :friendId AND fr.status = 0) OR " +
            "(fr.user.id = :friendId AND fr.friend.id = :userId AND fr.status = 0)")
    FriendRequest getPendingFriendRequestByUserIdAndFriendId(@Param("userId") Integer userId, @Param("friendId") Integer friendId);

    @Modifying
    @Query("DELETE FROM FriendRequest fr " +
            "WHERE (fr.user.id = :userId AND fr.friend.id = :friendId) OR " +
            "(fr.user.id = :friendId AND fr.friend.id = :userId)")
    @Transactional
    void deleteFriendRequestsByUserIdAndFriendId(@Param("userId") Integer userId, @Param("friendId") Integer friendId);

}

