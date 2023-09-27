package com.example.ConnectUs.service;

import com.example.ConnectUs.dto.friendRequest.ProcessRequestDTO;
import com.example.ConnectUs.enumerations.FriendRequestStatus;
import com.example.ConnectUs.exceptions.DatabaseAccessException;
import com.example.ConnectUs.model.postgres.FriendRequest;
import com.example.ConnectUs.model.postgres.User;
import com.example.ConnectUs.repository.postgres.FriendRequestRepository;
import com.example.ConnectUs.repository.postgres.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendRequestService {
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;

    public boolean addFriend(Integer userId, Integer friendId){
        try{
            User user = userRepository.findById(userId).orElseThrow();
            User friend = userRepository.findById(friendId).orElseThrow();
            FriendRequest friendRequest = FriendRequest.builder()
                    .user(user)
                    .friend(friend)
                    .status(FriendRequestStatus.PENDING)
                    .build();
            friendRequestRepository.save(friendRequest);
            return true;
        }catch (DataAccessException e){
            throw new DatabaseAccessException("Error when accessing the database.");
        }
    }

    @Transactional
    public FriendRequest processRequest(ProcessRequestDTO processRequestDTO) {
        try{
            FriendRequest friendRequest = friendRequestRepository.findById(processRequestDTO.getRequestId()).orElseThrow();
            if(processRequestDTO.isAccepted()){
                friendRequest.setStatus(FriendRequestStatus.ACCEPTED);
                User user = userRepository.findById(friendRequest.getUser().getId()).orElseThrow();
                User friend = userRepository.findById(friendRequest.getFriend().getId()).orElseThrow();

                List<User> userFriends = user.getFriends();
                userFriends.add(friend);
                user.setFriends(userFriends);
                userRepository.save(user);

                List<User> friendFriends = friend.getFriends();
                friendFriends.add(user);
                friend.setFriends(friendFriends);
                userRepository.save(friend);

            }else{
                friendRequest.setStatus(FriendRequestStatus.REJECTED);
            }
            FriendRequest savedFriendRequest = friendRequestRepository.save(friendRequest);
            return savedFriendRequest;
        }catch (DataAccessException e){
            throw new DatabaseAccessException(e.getMessage());
        }
    }
}
