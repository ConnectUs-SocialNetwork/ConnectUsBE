package com.example.ConnectUs.repository.neo4j;

import com.example.ConnectUs.model.neo4j.PageNeo4j;
import com.example.ConnectUs.model.postgres.Post;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

public interface PageNeo4jRepository extends Neo4jRepository<PageNeo4j, Long> {

    @Query("MATCH (p:page)-[:LIKED_BY]->(u:user) " +
            "WHERE p.id = $pageId " +
            "RETURN count(u)")
    int getNumberOfLikes(@Param("pageId") Long pageId);

    @Query("MATCH (user:user)-[:LIKED_BY]->(page:page) " +
            "WHERE user.id = $userId AND page.id = $pageId " +
            "RETURN count(page) > 0")
    boolean isLikedByUser(@Param("pageId") Long pageId, @Param("userId") Long userId);

}
