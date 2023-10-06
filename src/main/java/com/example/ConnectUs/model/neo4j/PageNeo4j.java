package com.example.ConnectUs.model.neo4j;

import com.example.ConnectUs.enumerations.PageCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.cypherdsl.core.Neo4jVersion;
import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Node("page")
public class PageNeo4j {
    @Id
    private Long id;
    private PageCategory category;

    @Relationship(type = "LIKED_BY")
    List<UserNeo4j> usersWhoLikedPage;
}
