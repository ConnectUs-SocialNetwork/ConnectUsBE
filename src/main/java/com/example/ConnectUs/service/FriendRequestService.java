package com.example.ConnectUs.service;

import com.example.ConnectUs.dto.friendRequest.FriendRequestDTO;
import com.example.ConnectUs.dto.friendRequest.ProcessFriendRequestResponse;
import com.example.ConnectUs.dto.friendRequest.ProcessRequestDTO;
import com.example.ConnectUs.enumerations.FriendRequestStatus;
import com.example.ConnectUs.enumerations.NotificationType;
import com.example.ConnectUs.exceptions.DatabaseAccessException;
import com.example.ConnectUs.model.neo4j.UserNeo4j;
import com.example.ConnectUs.model.postgres.FriendRequest;
import com.example.ConnectUs.model.postgres.Notification;
import com.example.ConnectUs.model.postgres.User;
import com.example.ConnectUs.repository.neo4j.UserNeo4jRepository;
import com.example.ConnectUs.repository.postgres.FriendRequestRepository;
import com.example.ConnectUs.repository.postgres.NotificationRepository;
import com.example.ConnectUs.repository.postgres.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendRequestService {
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;
    private final UserNeo4jRepository userNeo4jRepository;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @Transactional(value = "chainedTransactionManager")
    public boolean addFriend(Integer userId, Integer friendId) {
        try {
            User user = userRepository.findById(userId).orElseThrow();
            User friend = userRepository.findById(friendId).orElseThrow();
            FriendRequest friendRequest = FriendRequest.builder()
                    .user(user)
                    .friend(friend)
                    .status(FriendRequestStatus.PENDING)
                    .build();
            friendRequestRepository.save(friendRequest);

            UserNeo4j userNeo4j = userNeo4jRepository.findUserById(userId);
            UserNeo4j friendNeo4j = userNeo4jRepository.findUserById(friendId);

            List<UserNeo4j> usersToWhomISentTheRequest = userNeo4j.getSentFriendRequests();
            usersToWhomISentTheRequest.add(friendNeo4j);
            userNeo4j.setSentFriendRequests(usersToWhomISentTheRequest);
            userNeo4jRepository.save(userNeo4j);

            notificationService.save(Notification.builder()
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .user(friend)
                    .avatar(user.getProfileImage())
                    .type(NotificationType.FRIEND_REQUEST)
                    .dateAndTime(LocalDateTime.now())
                    .entityId(userId)
                    .isRead(false)
                    .text("send you a friend request. Click on the notification to see user profile.")
                    .requestId(friendRequest.getId())
                    .build());

            return true;
        } catch (DataAccessException e) {
            throw new DatabaseAccessException("Error when accessing the database.");
        }
    }

    @Transactional
    public FriendRequest processRequest(ProcessRequestDTO processRequestDTO) {
        try {
            FriendRequest friendRequest = friendRequestRepository.findById(processRequestDTO.getRequestId()).orElseThrow();
            return processFriendship(processRequestDTO, friendRequest);
        } catch (DataAccessException e) {
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    private void saveAcceptedFriendRequestNotification(User user, User friend) {
        notificationService.save(Notification.builder()
                .firstname(friend.getFirstname())
                .lastname(friend.getLastname())
                .user(user)
                .avatar(friend.getProfileImage())
                .type(NotificationType.FRIEND_REQUEST_ACCEPTED)
                .dateAndTime(LocalDateTime.now())
                .entityId(friend.getId())
                .isRead(false)
                .text("accept your friend request. Click on the notification to see " + friend.getFirstname() + " " + friend.getLastname() + " profile.")
                .build());
    }

    @Transactional(value = "chainedTransactionManager")
    private FriendRequest processFriendship(ProcessRequestDTO processRequestDTO, FriendRequest friendRequest) {
        if (processRequestDTO.isAccepted()) {
            friendRequest.setStatus(FriendRequestStatus.ACCEPTED);

            User user = userRepository.findById(friendRequest.getUser().getId()).orElseThrow();
            UserNeo4j userNeo4j = userNeo4jRepository.findUserById(friendRequest.getUser().getId());

            User friend = userRepository.findById(friendRequest.getFriend().getId()).orElseThrow();
            UserNeo4j friendNeo4j = userNeo4jRepository.findUserById(friendRequest.getFriend().getId());

            userNeo4jRepository.deleteFriendRequest(friendRequest.getUser().getId().longValue(), friendRequest.getFriend().getId().longValue());

            savePostgresFriend(user, friend);
            saveNeo4jFriend(userNeo4j, friendNeo4j);

            saveAcceptedFriendRequestNotification(user, friend);

            notificationRepository.deleteFriendRequestNotificationsByRequestId(friendRequest.getId());

        } else {
            friendRequest.setStatus(FriendRequestStatus.REJECTED);
            userNeo4jRepository.deleteFriendRequest(friendRequest.getUser().getId().longValue(), friendRequest.getFriend().getId().longValue());
            notificationRepository.deleteFriendRequestNotificationsByRequestId(friendRequest.getId());
        }
        FriendRequest savedFriendRequest = friendRequestRepository.save(friendRequest);
        return savedFriendRequest;
    }

    private void savePostgresFriend(User user, User friend) {
        //User postgres
        List<User> userFriends = user.getFriends();
        userFriends.add(friend);
        user.setFriends(userFriends);
        userRepository.save(user);

        //Friend postgres
        List<User> friendFriends = friend.getFriends();
        friendFriends.add(user);
        friend.setFriends(friendFriends);
        userRepository.save(friend);
    }

    private void saveNeo4jFriend(UserNeo4j userNeo4j, UserNeo4j friendNeo4j) {
        //User neo4j
        List<UserNeo4j> userNeo4jFriends = userNeo4j.getFriends();
        userNeo4jFriends.add(friendNeo4j);
        userNeo4j.setFriends(userNeo4jFriends);
        userNeo4jRepository.save(userNeo4j);

        //Friend neo4j
        List<UserNeo4j> friendNeo4jFriends = friendNeo4j.getFriends();
        friendNeo4jFriends.add(userNeo4j);
        friendNeo4j.setFriends(friendNeo4jFriends);
        userNeo4jRepository.save(friendNeo4j);
    }

    @Transactional
    public void unsendRequest(FriendRequestDTO data) {
        FriendRequest fr = friendRequestRepository.getPendingFriendRequestByUserIdAndFriendId(data.getUserId(), data.getFriendId());
        friendRequestRepository.deleteByUserIdAndFriendId(data.getFriendId(), data.getUserId());
        notificationRepository.deleteFriendRequestNotificationsByEntityId(fr.getId());

        userNeo4jRepository.deleteFriendRequest(fr.getUser().getId().longValue(), fr.getFriend().getId().longValue());;
    }
}
