package com.example.ConnectUs.model.neo4j;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Node("user")
public class UserNeo4j {

    @Id
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String profileImage;
    private String number;
    private String street;
    private String city;
    private String country;

    @Relationship(type = "FRIENDS_WITH")
    private List<UserNeo4j> friends;

    @Relationship(type = "SEND_FRIEND_REQUEST", direction = Relationship.Direction.OUTGOING)
    private List<UserNeo4j> sentFriendRequests;
}
