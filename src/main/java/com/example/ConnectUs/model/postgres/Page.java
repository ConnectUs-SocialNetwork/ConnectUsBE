package com.example.ConnectUs.model.postgres;

import com.example.ConnectUs.enumerations.PageCategory;
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
@Table(name = "page")
public class Page {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private PageCategory category;
    private String avatar;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User administrator;
}
