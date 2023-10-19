package com.example.ConnectUs.repository.postgres;

import com.example.ConnectUs.model.postgres.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Integer> {
}
