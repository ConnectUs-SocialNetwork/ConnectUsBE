package com.example.ConnectUs.repository.neo4j;

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
}
