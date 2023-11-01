package com.example.ConnectUs.repository.neo4j;

import com.example.ConnectUs.dto.user.RecommendedUserResponse;
import com.example.ConnectUs.model.neo4j.UserNeo4j;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNeo4jRepository extends Neo4jRepository<UserNeo4j, Long> {
    @Query("MATCH (u:user)-[:FRIENDS_WITH]->(commonFriend:user)<-[:FRIENDS_WITH]-(me:user) " +
            "WHERE u.id = $userId AND me.id = $myId " +
            "RETURN commonFriend")
    List<UserNeo4j> findMutualFriends(@Param("userId") Integer userId, @Param("myId") Integer myId);

    @Query("MATCH (user:user)-[:FRIENDS_WITH]->(friend:user) WHERE user.id = $userId RETURN friend")
    List<UserNeo4j> findUserFriends(@Param("userId") Long userId);
    @Query("MATCH (u:user)" +
            "WHERE any(word IN split($searchText, \" \") WHERE toLower(u.firstname) CONTAINS toLower(word) OR toLower(u.lastname) CONTAINS toLower(word))" +
            "RETURN u")
    List<UserNeo4j> findUsersBySearchText(@Param("searchText") String searchText);

    @Query("MATCH (u:user) WHERE u.id = $userId RETURN u")
    UserNeo4j findUserById(@Param("userId") Integer userId);
    @Query("MATCH (user:user)-[:FRIENDS_WITH]->(friend:user) " +
            "WHERE user.id = $userId " +
            "RETURN count(friend) AS numberOfFriends")
    int getNumberOfUserFriends(@Param("userId") Integer userId);

    @Query("MATCH (u:user)-[:FRIENDS_WITH]->(commonFriend:user)<-[:FRIENDS_WITH]-(me:user) " +
            "WHERE u.id = $userId AND me.id = $myId " +
            "RETURN count(commonFriend)")
    int getNumberOfMutualFriends(@Param("userId") Integer userId, @Param("myId") Integer myId);

    @Query("MATCH (u:user)-[r:FRIENDS_WITH]->(f:user) " +
            "WHERE (u.id = $userId AND f.id = $friendId) OR " +
            "(u.id = $friendId AND f.id = $userId) " +
            "DELETE r")
    void removeFriendsRelation(@Param("userId") Long userId, @Param("friendId") Long friendId);

    @Query("MATCH (user:user {id: $userID})-[:FRIENDS_WITH]->(userFriend:user)\n" +
            "MATCH (friendFriend:user)-[:FRIENDS_WITH]->(userFriend)\n" +
            "WHERE NOT (user)-[:SEND_FRIEND_REQUEST]->(userFriend) AND userFriend.id <> $userID \n" +
            "WITH friendFriend, COLLECT(userFriend) AS commonFriends\n" +
            "WHERE SIZE(commonFriends) >= 5\n" +
            "AND NOT friendFriend.id = $userID\n" +
            "RETURN DISTINCT friendFriend.id")
    List<Long> recommendFriendsOfMyFriends(@Param("userID") Long userID);

    @Query("MATCH (currentUser:user {id: $yourId}) " +
            "MATCH (user:user) " +
            "WHERE user.id IN $userIds " +
            "WITH currentUser, user, [(currentUser)-[:FRIENDS_WITH]-(friend) | friend] AS yourFriends, [(user)-[:FRIENDS_WITH]-(friend) | friend] AS userFriends " +
            "RETURN user.id AS id, user.firstname AS firstname, user.lastname AS lastname, " +
            "user.email AS email, user.profileImage AS profileImage, " +
            "size(userFriends)/2 AS numberOfFriends, " +
            "user.country AS country, user.city AS city, " +
            "user.street AS street, user.number AS number, " +
            "size([friend IN yourFriends WHERE friend IN userFriends])/2 AS numberOfMutualFriends")
    List<RecommendedUserResponse> findRecommendedUsers(@Param("yourId") Long yourId, @Param("userIds") List<Long> userIds);

    @Query("MATCH (currentUser:user {id: $yourId})-[:FRIENDS_WITH]->(userFriend:user) " +
            "MATCH (friendOfFriend:user)-[:FRIENDS_WITH]->(userFriend) " +
            "WHERE friendOfFriend.id <> $yourId " +
            "WITH DISTINCT friendOfFriend, currentUser " +
            "MATCH (likedPage:page)-[:LIKED_BY]->(currentUser:user {id: $yourId}) " +
            "MATCH (p:page {category: likedPage.category}) " +
            "WITH friendOfFriend, p as correspondingPages, currentUser " +
            "MATCH (correspondingPages)-[:LIKED_BY]->(potentialFriend:user {id: friendOfFriend.id}) " +
            "WHERE NOT (currentUser)-[:SEND_FRIEND_REQUEST]->(potentialFriend)" +
            "return potentialFriend.id ")
    List<Long> recommendUsersBasedOnTheirInterest(@Param("yourId") Long yourId);

    @Query("MATCH (currentUser:user {id: $yourId}) " +
            "MATCH (user:user) " +
            "WHERE NOT (currentUser)-[:FRIENDS_WITH]-(user) AND user.id <> $yourId " +
            "AND NOT (currentUser)-[:SEND_FRIEND_REQUEST]->(user) " +
            "RETURN user.id " +
            "LIMIT 50")
    List<Long> findSupplementaryRecommendations(@Param("yourId") Long yourId);


    @Query("MATCH (user:user {id: $userId})-[r:SEND_FRIEND_REQUEST]->(friend:user {id: $friendId}) DELETE r")
    void deleteFriendRequest(Long userId, Long friendId);
}
