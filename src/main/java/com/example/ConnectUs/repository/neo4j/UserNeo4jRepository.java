package com.example.ConnectUs.repository.neo4j;

import com.example.ConnectUs.model.neo4j.UserNeo4j;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNeo4jRepository extends Neo4jRepository<UserNeo4j, Long> {
    @Query("MATCH (u:UserNeo4j)-[:FRIENDS_WITH]->(commonFriend:UserNeo4j)<-[:FRIENDS_WITH]-(me:UserNeo4j) " +
            "WHERE id(u) = $userId AND id(me) = $myId " +
            "RETURN commonFriend")
    List<UserNeo4j> findMutualFriends(@Param("userId") Long userId, @Param("myId") Long myId);
}
