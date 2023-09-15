package com.example.ConnectUs.repository.neo4j;

import com.example.ConnectUs.model.neo4j.UserNeo4j;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserNeo4jRepository extends Neo4jRepository<UserNeo4j, Long> {
}
