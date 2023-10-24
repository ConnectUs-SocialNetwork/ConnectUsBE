package com.example.ConnectUs.service;

import com.example.ConnectUs.dto.authentication.UserResponse;
import com.example.ConnectUs.dto.searchUsers.SearchUserResponse;
import com.example.ConnectUs.dto.user.RecommendedUserResponse;
import com.example.ConnectUs.dto.user.UpdateUserRequest;
import com.example.ConnectUs.dto.user.UpdateUserResponse;
import com.example.ConnectUs.dto.user.UserProfileResponse;
import com.example.ConnectUs.enumerations.Gender;
import com.example.ConnectUs.exceptions.DatabaseAccessException;
import com.example.ConnectUs.model.mongo.UserMongo;
import com.example.ConnectUs.model.neo4j.UserNeo4j;
import com.example.ConnectUs.model.postgres.User;
import com.example.ConnectUs.repository.mongo.UserMongoRepository;
import com.example.ConnectUs.repository.neo4j.UserNeo4jRepository;
import com.example.ConnectUs.repository.postgres.FriendRequestRepository;
import com.example.ConnectUs.repository.postgres.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserNeo4jRepository userNeo4jRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final UserMongoRepository userMongoRepository;
    private final MongoTemplate mongoTemplate;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findById(Integer userId) {
        return userRepository.findById(userId).orElseThrow();
    }

    public List<SearchUserResponse> searchUsers(Integer userId, String searchText) {
        try {
            List<UserNeo4j> userFriends = userNeo4jRepository.findUserFriends(userId.longValue());
            List<UserNeo4j> userList = userNeo4jRepository.findUsersBySearchText(searchText);
            userList.sort(Comparator.comparing(u -> !userFriends.contains(u)));
            List<SearchUserResponse> responseList = new ArrayList<>();
            for (UserNeo4j u : userList) {
                SearchUserResponse searchUserResponse = SearchUserResponse.builder()
                        .id(u.getId().intValue())
                        .profileImage(u.getProfileImage())
                        .friend(userFriends.contains(u))
                        .email(u.getEmail())
                        .firstname(u.getFirstname())
                        .lastname(u.getLastname())
                        .build();
                if (!searchUserResponse.isFriend()) {
                    searchUserResponse.setNumberOfFriends(userNeo4jRepository.getNumberOfUserFriends(u.getId().intValue()));
                    searchUserResponse.setNumberOfMutualFriends(userNeo4jRepository.getNumberOfMutualFriends(u.getId().intValue(), userId.intValue()));
                }
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
                if (!searchUserResponse.isFriend()) {
                    searchUserResponse.setNumberOfFriends(userNeo4jRepository.getNumberOfUserFriends(u.getId().intValue()));
                    searchUserResponse.setNumberOfMutualFriends(userNeo4jRepository.getNumberOfMutualFriends(u.getId().intValue(), userId.intValue()));
                }
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
                if (!userResponse.isFriend()) {
                    userResponse.setNumberOfFriends(userNeo4jRepository.getNumberOfUserFriends(u.getId().intValue()));
                    userResponse.setNumberOfMutualFriends(userNeo4jRepository.getNumberOfMutualFriends(u.getId().intValue(), userId.intValue()));
                }

                responseList.add(userResponse);
            }

            return responseList;
        } catch (DataAccessException e) {
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    public UserProfileResponse getUserProfileResponse(Integer userId, Integer myId) {
        try {
            User user = userRepository.findById(userId).orElseThrow();
            User myUser = userRepository.findById(myId).orElseThrow();

            boolean isFriends = user.getFriends().contains(myUser);
            boolean iSentFriendRequest = friendRequestRepository.hasPendingFriendRequest(myId, userId);
            boolean heSentFriendRequest = friendRequestRepository.hasPendingFriendRequest(userId, myId);
            int numberOfFriends = userNeo4jRepository.getNumberOfUserFriends(userId);
            int numberOfMutualFriends = userNeo4jRepository.getNumberOfMutualFriends(userId, myId);
            String dob = formatDate(user.getDateOfBirth());
            Integer requestId = -1;

            if (heSentFriendRequest) {
                requestId = friendRequestRepository.getPendingFriendRequestByUserIdAndFriendId(user.getId(), myUser.getId()).getId();
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
        } catch (DataAccessException e) {
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    public String formatDate(LocalDate dateOfBirth) {
        int day = dateOfBirth.getDayOfMonth();
        String dayWithSuffix = getDayWithSuffix(day);
        String monthAndYear = dateOfBirth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH));
        String formattedDate = dayWithSuffix + " " + monthAndYear;
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

    public static LocalDate parseDate(String dateString) {
        // Definirajte obrazac za parsiranje datuma iz stringa
        Pattern pattern = Pattern.compile("(\\d+)\\s+([A-Za-z]+)\\s+(\\d+)");
        Matcher matcher = pattern.matcher(dateString);

        if (matcher.matches()) {
            // Dobijte dan, mjesec i godinu iz podudaranja
            int day = Integer.parseInt(matcher.group(1));
            String month = matcher.group(2);
            int year = Integer.parseInt(matcher.group(3));

            // Pretvorite string mjeseca u datumski objekt
            LocalDate date = LocalDate.parse(month + " " + day + " " + year, DateTimeFormatter.ofPattern("MMMM d yyyy", Locale.ENGLISH));
            return date;
        } else {
            // Ako parsiranje nije uspjelo, možete rukovati greškom ili vratiti null
            return null;
        }
    }

    private void removePostgresFriend(User user, User friend) {
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

    private void removeNeo4jFriend(UserNeo4j userNeo4j, UserNeo4j friendNeo4j) {
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
    public void removeFriend(Integer userId, Integer friendId) {
        try {
            friendRequestRepository.deleteFriendRequestsByUserIdAndFriendId(userId, friendId);

            User user = userRepository.findById(userId).orElseThrow();
            UserNeo4j userNeo4j = userNeo4jRepository.findUserById(userId);

            User friend = userRepository.findById(friendId).orElseThrow();
            UserNeo4j friendNeo4j = userNeo4jRepository.findUserById(friendId);

            removePostgresFriend(user, friend);
            userNeo4jRepository.removeFriendsRelation(user.getId().longValue(), friend.getId().longValue());
        } catch (DataAccessException e) {
            throw new DatabaseAccessException(e.getMessage());
        }
    }

    @Transactional(value = "chainedTransactionManager")
    public UpdateUserResponse updateUser(UpdateUserRequest updateUserRequest) {
        User user = userRepository.findById(updateUserRequest.getId()).orElseThrow();
        UserNeo4j userNeo4j = userNeo4jRepository.findUserById(updateUserRequest.getId());

        user.setEmail(updateUserRequest.getEmail());
        user.setDateOfBirth(LocalDate.parse(updateUserRequest.getDateOfBirth()));
        user.setGender(Gender.valueOf(updateUserRequest.getGender().toUpperCase()));
        user.setFirstname(updateUserRequest.getFirstname());
        user.setLastname(updateUserRequest.getLastname());
        user.setProfileImage(user.getProfileImage());

        userNeo4j.setEmail(updateUserRequest.getEmail());
        userNeo4j.setFirstname(updateUserRequest.getFirstname());
        userNeo4j.setLastname(updateUserRequest.getLastname());

        if (updateUserRequest.getProfileImage() == null) {
            if(userRepository.findByEmail(updateUserRequest.getEmail()).isPresent() && user.getEmail() != updateUserRequest.getEmail()){
                return UpdateUserResponse.builder()
                        .userResponse(null)
                        .message("The entered email is already in use!")
                        .build();
            }
            userRepository.save(user);
            userNeo4jRepository.save(userNeo4j);
        } else {
            user.setProfileImage(updateUserRequest.getProfileImage());
            userNeo4j.setProfileImage(updateUserRequest.getProfileImage());
            userRepository.save(user);
            userNeo4jRepository.save(userNeo4j);
        }

        UserResponse userResponse = UserResponse.builder()
                .profileImage(user.getProfileImage())
                .id(user.getId())
                .gender(capitalizeFirstLetter(user.getGender().toString()))
                .dateOfBirth(user.getDateOfBirth().toString())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .build();

        return UpdateUserResponse.builder()
                .userResponse(userResponse)
                .message("Successfully!")
                .build();
    }

    public String capitalizeFirstLetter(String input) {
        if (input.isEmpty()) {
            return input;
        }

        String prvoSlovoVeliko = input.substring(0, 1).toUpperCase();
        String ostalaSlovaMala = input.substring(1).toLowerCase();

        return prvoSlovoVeliko + ostalaSlovaMala;
    }

    public List<Long> recommendUsersWithinXkm(Integer userId, Integer x) {
        UserMongo user = userMongoRepository.findById(userId).orElseThrow();
        Point point = new Point(user.getLocation().getX(), user.getLocation().getY());
        Distance distance = new Distance(x, Metrics.KILOMETERS);
        List<UserMongo> recommendedUsers = userMongoRepository.findByLocationNear(point, distance);

        return recommendedUsers.stream().map((UserMongo::getId)).collect(Collectors.toList()).stream().map(Integer::longValue).collect(Collectors.toList());
    }

    public List<Long> recommendFriendsOfMyFriends(Long userId){
        List<UserNeo4j> userList = userNeo4jRepository.recommendFriendsOfMyFriends(userId);
        return userList.stream().map(UserNeo4j::getId).collect(Collectors.toList());
    }

    public List<Long> recommendUsersBasedOnTheirInterest(Long userId){
        List<UserNeo4j> userList = userNeo4jRepository.recommendUsersBasedOnTheirInterest(userId);
        return userList.stream().map(UserNeo4j::getId).collect(Collectors.toList());
    }

    public List<RecommendedUserResponse> getRecommendedUsers(Long userId){
        List<Long> allRecommendedUserIds = new ArrayList<>();
        allRecommendedUserIds.addAll(recommendUsersWithinXkm(userId.intValue(), 10));
        allRecommendedUserIds.addAll(recommendFriendsOfMyFriends(userId));
        allRecommendedUserIds.addAll(recommendUsersBasedOnTheirInterest(userId));
        allRecommendedUserIds.remove(userId);

        return userNeo4jRepository.findRecommendedUsers(userId, allRecommendedUserIds);
    }


}
