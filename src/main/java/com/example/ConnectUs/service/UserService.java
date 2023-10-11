package com.example.ConnectUs.service;

import com.example.ConnectUs.dto.searchUsers.SearchUserResponse;
import com.example.ConnectUs.dto.user.UserProfileResponse;
import com.example.ConnectUs.exceptions.DatabaseAccessException;
import com.example.ConnectUs.model.neo4j.UserNeo4j;
import com.example.ConnectUs.model.postgres.FriendRequest;
import com.example.ConnectUs.model.postgres.User;
import com.example.ConnectUs.repository.neo4j.UserNeo4jRepository;
import com.example.ConnectUs.repository.postgres.FriendRequestRepository;
import com.example.ConnectUs.repository.postgres.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
            boolean iSentFriendRequest = friendRequestRepository.hasPendingFriendRequest(myId, userId);
            boolean heSentFriendRequest = friendRequestRepository.hasPendingFriendRequest(userId, myId);
            int numberOfFriends = userNeo4jRepository.getNumberOfUserFriends(userId);
            int numberOfMutualFriends = userNeo4jRepository.getNumberOfMutualFriends(userId, myId);
            String dob = formatDate(user.getDateOfBirth());
            Integer requestId = -1;

            if(heSentFriendRequest){
                requestId = friendRequestRepository.getFriendRequestByUserIdAndFriendId(user.getId(), myUser.getId()).getId();
            }

            return UserProfileResponse.builder()
                    .id(user.getId())
                    .friends(isFriends)
                    .profilePicture(user.getProfileImage())
                    .numberOfFriends(numberOfFriends)
                    .numberOfMutualFriends(numberOfMutualFriends)
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .requestSentByMe(iSentFriendRequest)
                    .heSentFriendRequest(heSentFriendRequest)
                    .dateOfBirth(dob)
                    .requestId(requestId)
                    .build();
        }catch (DataAccessException e){
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    private String formatDate(LocalDate dateOfBirth){
        int day = dateOfBirth.getDayOfMonth();
        String dayWithSuffix = getDayWithSuffix(day);
        String monthAndYear = dateOfBirth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH));
        String formattedDate = dayWithSuffix+ " " + monthAndYear;
        return formattedDate;
    }

    private String getDayWithSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return day + "th";
        }
        switch (day % 10) {
            case 1:
                return day + "st";
            case 2:
                return day + "nd";
            case 3:
                return day + "rd";
            default:
                return day + "th";
        }
    }

    private void removePostgresFriend(User user, User friend){
        //User postgres
        List<User> userFriends = user.getFriends();
        userFriends.remove(friend);
        user.setFriends(userFriends);
        userRepository.save(user);

        //Friend postgres
        List<User> friendFriends = friend.getFriends();
        friendFriends.remove(user);
        friend.setFriends(friendFriends);
        userRepository.save(friend);
    }

    private void removeNeo4jFriend(UserNeo4j userNeo4j, UserNeo4j friendNeo4j){
        //User neo4j
        List<UserNeo4j> userNeo4jFriends = userNeo4j.getFriends();
        userNeo4jFriends.remove(friendNeo4j);
        userNeo4j.setFriends(userNeo4jFriends);
        userNeo4jRepository.save(userNeo4j);

        //Friend neo4j
        List<UserNeo4j> friendNeo4jFriends = friendNeo4j.getFriends();
        friendNeo4jFriends.remove(userNeo4j);
        friendNeo4j.setFriends(friendNeo4jFriends);
        userNeo4jRepository.save(friendNeo4j);
    }

    @Transactional(value = "chainedTransactionManager")
    public void removeFriend(Integer userId, Integer friendId){
        try{
            friendRequestRepository.deleteFriendRequestsByUserIdAndFriendId(userId, friendId);

            User user = userRepository.findById(userId).orElseThrow();
            UserNeo4j userNeo4j = userNeo4jRepository.findUserById(userId);

            User friend = userRepository.findById(friendId).orElseThrow();
            UserNeo4j friendNeo4j = userNeo4jRepository.findUserById(friendId);

            removePostgresFriend(user, friend);
            userNeo4jRepository.removeFriendsRelation(user.getId().longValue(), friend.getId().longValue());
        }catch (DataAccessException e){
            throw new DatabaseAccessException(e.getMessage());
        }
    }
}
