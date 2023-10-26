package com.example.ConnectUs.repository.neo4j;

import com.example.ConnectUs.model.neo4j.PageNeo4j;
import com.example.ConnectUs.model.neo4j.UserNeo4j;
import com.example.ConnectUs.model.postgres.Post;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PageNeo4jRepository extends Neo4jRepository<PageNeo4j, Long> {

    @Query("MATCH (p:page)-[:LIKED_BY]->(u:user) " +
            "WHERE p.id = $pageId " +
            "RETURN count(u)")
    int getNumberOfLikes(@Param("pageId") Long pageId);

    @Query("MATCH (page:page)-[:LIKED_BY]->(user:user) " +
            "WHERE user.id = $userId AND page.id = $pageId " +
            "RETURN count(page) > 0")
    boolean isLikedByUser(@Param("pageId") Long pageId, @Param("userId") Long userId);

    @Query("MATCH (p:page) WHERE p.id = $pageId RETURN p")
    PageNeo4j findPageById(@Param("pageId") Integer pageId);

    @Query("MATCH (p:page)-[r:LIKED_BY]->(u:user) WHERE u.id = $userId AND p.id = $pageId DELETE r")
    void unlikePage(@Param("pageId") Long pageId, @Param("userId") Long userId);

    @Query("MATCH (p:page)-[r:LIKED_BY]->(u:user) WHERE p.id = $pageId return u.id as id, u.firstname as firstname, u.lastname as lastname, u.email as email, u.profileImage as profileImage, u.friends as friends")
    List<UserNeo4j> getLikers(@Param("pageId") Integer pageId);

    @Query("MATCH (p:page)-[r:LIKED_BY]->(u:user) WHERE u.id = $userId return p.id as id")
    List<Long> getLikedPagesIds(@Param("userId") Integer userId);

    @Query("MATCH (p:page)-[r:LIKED_BY]->(u:user) WHERE p.id = $pageId RETURN COUNT(r) AS numberOfLikes")
    Integer getNumberOfLikes(@Param("pageId") Integer pageId);

    @Query("MATCH (user: user {id: $yourId})-[:FRIENDS_WITH]->(friend:user) " +
            "MATCH (page:page)-[:LIKED_BY]->(friend) " +
            "WHERE NOT (page)-[:LIKED_BY]->(user) " +
            "RETURN DISTINCT page.id")
    List<Long> recommendPagesThatHaveBeenLikedByMyFriends(@Param("yourId") Long yourId);

    @Query("MATCH (user:user {id: $yourId}) " +
            "MATCH (page:page) " +
            "WHERE NOT (page)-[:LIKED_BY]->(user) AND NOT page.id IN $pageIds " +
            "RETURN DISTINCT page.id")
    List<Long> getSupplementaryPages(@Param("yourId") Long yourId, @Param("pageIds") List<Long> pageIds);
}
