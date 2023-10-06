package com.example.ConnectUs.service;

import com.example.ConnectUs.dto.searchUsers.SearchUserResponse;
import com.example.ConnectUs.dto.user.UserProfileResponse;
import com.example.ConnectUs.exceptions.DatabaseAccessException;
import com.example.ConnectUs.model.neo4j.UserNeo4j;
import com.example.ConnectUs.model.postgres.User;
import com.example.ConnectUs.repository.neo4j.UserNeo4jRepository;
import com.example.ConnectUs.repository.postgres.FriendRequestRepository;
import com.example.ConnectUs.repository.postgres.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserNeo4jRepository userNeo4jRepository;
    private final FriendRequestRepository friendRequestRepository;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<SearchUserResponse> searchUsers(Integer userId, String searchText) {
        try {
            UserNeo4j user = userNeo4jRepository.findUserById(userId);
            List<UserNeo4j> userList = userNeo4jRepository.findUsersBySearchText(searchText);
            userList.sort(Comparator.comparing(u -> !user.getFriends().contains(u)));
            List<SearchUserResponse> responseList = new ArrayList<>();
            for (UserNeo4j u : userList) {
                SearchUserResponse searchUserResponse = SearchUserResponse.builder()
                        .id(u.getId().intValue())
                        .profileImage(u.getProfileImage())
                        .friend(user.getFriends().contains(u))
                        .email(u.getEmail())
                        .firstname(u.getFirstname())
                        .lastname(u.getLastname())
                        .build();
                responseList.add(searchUserResponse);
            }
            return responseList;
        } catch (DataAccessException e) {
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    @Transactional
    public List<SearchUserResponse> getUserFriends(Integer userId, Integer myId) {
        try {
            User user = userRepository.findById(userId).orElseThrow();
            User myUser = userRepository.findById(myId).orElseThrow();

            List<SearchUserResponse> responseList = new ArrayList<>();
            for (User u : user.getFriends()) {
                SearchUserResponse searchUserResponse = SearchUserResponse.builder()
                        .id(u.getId())
                        .profileImage(u.getProfileImage())
                        .friend(myUser.getFriends().contains(u))
                        .email(u.getEmail())
                        .firstname(u.getFirstname())
                        .lastname(u.getLastname())
                        .build();
                responseList.add(searchUserResponse);
            }
            return responseList;
        } catch (DataAccessException e) {
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    public List<SearchUserResponse> getUserMutualFriends(Integer userId, Integer myId) {
        try {
            List<UserNeo4j> mutualFriends = userNeo4jRepository.findMutualFriends(userId, myId);
            List<SearchUserResponse> responseList = new ArrayList<>();

            for (UserNeo4j u : mutualFriends) {
                SearchUserResponse userResponse = SearchUserResponse.builder()
                        .id(u.getId().intValue())
                        .friend(true)
                        .firstname(u.getFirstname())
                        .lastname(u.getLastname())
                        .email(u.getEmail())
                        .profileImage(u.getProfileImage())
                        .build();

                responseList.add(userResponse);
            }

            return responseList;
        } catch (DataAccessException e) {
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    public UserProfileResponse getUserProfileResponse(Integer userId, Integer myId) {
        try{
            User user = userRepository.findById(userId).orElseThrow();
            User myUser = userRepository.findById(myId).orElseThrow();

            boolean isFriends = user.getFriends().contains(myUser);
            boolean isRequested = friendRequestRepository.hasPendingFriendRequest(myId, userId);
            int numberOfFriends = userNeo4jRepository.getNumberOfUserFriends(userId);
            int numberOfMutualFriends = userNeo4jRepository.getNumberOfMutualFriends(userId, myId);

            return UserProfileResponse.builder()
                    .id(user.getId())
                    .friends(isFriends)
                    .profilePicture(user.getProfileImage())
                    .numberOfFriends(numberOfFriends)
                    .numberOfMutualFriends(numberOfMutualFriends)
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .requested(isRequested)
                    .build();
        }catch (DataAccessException e){
            throw new DatabaseAccessException(e.getMessage());
        }
    }
}
