package com.example.ConnectUs.service;

import com.example.ConnectUs.dto.searchUsers.SearchUserResponse;
import com.example.ConnectUs.exceptions.DatabaseAccessException;
import com.example.ConnectUs.model.neo4j.UserNeo4j;
import com.example.ConnectUs.model.postgres.User;
import com.example.ConnectUs.repository.neo4j.UserNeo4jRepository;
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

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<SearchUserResponse> searchUsers(Integer userId, String searchText) {
        try{
            User user = userRepository.findById(userId).orElseThrow();
            String[] searchTextArray = searchText.split(" ");
            List<User> userList = userRepository.findFriendsAndNonFriendsBySearchText(searchText, userId);
            userList.sort(Comparator.comparing(u -> !user.getFriends().contains(u)));
            List<SearchUserResponse> responseList = new ArrayList<>();
            for(User u : userList) {
                SearchUserResponse searchUserResponse = SearchUserResponse.builder()
                        .id(u.getId())
                        .profileImage(u.getProfileImage())
                        .friend(user.getFriends().contains(u))
                        .email(u.getEmail())
                        .firstname(u.getFirstname())
                        .lastname(u.getLastname())
                        .build();
                responseList.add(searchUserResponse);
            }
            return responseList;
        }catch (DataAccessException e){
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    @Transactional
    public List<SearchUserResponse> getUserFriends(Integer userId, Integer myId){
        try{
            User user = userRepository.findById(userId).orElseThrow();
            User myUser = userRepository.findById(myId).orElseThrow();

            List<SearchUserResponse> responseList = new ArrayList<>();
            for(User u : user.getFriends()) {
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
        }catch(DataAccessException e){
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    public List<SearchUserResponse> getUserMutualFriends(Integer userId, Integer myId){
        try{
            List<UserNeo4j> mutualFriends = userNeo4jRepository.findMutualFriends(userId.longValue(), myId.longValue());
            List<SearchUserResponse> responseList = new ArrayList<>();

            for(UserNeo4j u : mutualFriends){
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
        }catch (DataAccessException e){
            throw  new DatabaseAccessException(e.getMessage());
        }
    }
}
