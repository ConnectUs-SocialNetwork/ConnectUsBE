package com.example.ConnectUs.model.postgres;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "location")
public class Location {
    @Id
    @GeneratedValue
    private Integer id;
    private String number;
    private String street;
    private String city;
    private String country;
    private Integer postalNumber;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
