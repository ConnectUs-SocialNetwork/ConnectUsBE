package com.example.ConnectUs.service;

import com.example.ConnectUs.dto.friendRequest.FriendRequestDTO;
import com.example.ConnectUs.dto.friendRequest.ProcessFriendRequestResponse;
import com.example.ConnectUs.dto.friendRequest.ProcessRequestDTO;
import com.example.ConnectUs.enumerations.FriendRequestStatus;
import com.example.ConnectUs.exceptions.DatabaseAccessException;
import com.example.ConnectUs.model.neo4j.UserNeo4j;
import com.example.ConnectUs.model.postgres.FriendRequest;
import com.example.ConnectUs.model.postgres.User;
import com.example.ConnectUs.repository.neo4j.UserNeo4jRepository;
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
    private final UserNeo4jRepository userNeo4jRepository;

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
            return processFriendship(processRequestDTO, friendRequest);
        }catch (DataAccessException e){
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    @Transactional(value = "chainedTransactionManager")
    private FriendRequest processFriendship(ProcessRequestDTO processRequestDTO, FriendRequest friendRequest){
        if(processRequestDTO.isAccepted()){
            friendRequest.setStatus(FriendRequestStatus.ACCEPTED);

            User user = userRepository.findById(friendRequest.getUser().getId()).orElseThrow();
            UserNeo4j userNeo4j = userNeo4jRepository.findUserById(friendRequest.getUser().getId());

            User friend = userRepository.findById(friendRequest.getFriend().getId()).orElseThrow();
            UserNeo4j friendNeo4j = userNeo4jRepository.findUserById(friendRequest.getFriend().getId());

            savePostgresFriend(user, friend);
            saveNeo4jFriend(userNeo4j, friendNeo4j);

        }else{
            friendRequest.setStatus(FriendRequestStatus.REJECTED);
        }
        FriendRequest savedFriendRequest = friendRequestRepository.save(friendRequest);
        return savedFriendRequest;
    }

    private void savePostgresFriend(User user, User friend){
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

    private void saveNeo4jFriend(UserNeo4j userNeo4j, UserNeo4j friendNeo4j){
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

    public void unsendRequest(FriendRequestDTO data){
        friendRequestRepository.deleteByUserIdAndFriendId(data.getFriendId(), data.getUserId());
    }
}
