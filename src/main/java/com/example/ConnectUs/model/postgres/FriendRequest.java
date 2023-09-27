package com.example.ConnectUs.model.postgres;

import com.example.ConnectUs.enumerations.FriendRequestStatus;
import jakarta.persistence.*;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_friendRequest")
public class FriendRequest {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "friend_id")
    private User friend;

    @Column(name = "status")
    private FriendRequestStatus status;

}
