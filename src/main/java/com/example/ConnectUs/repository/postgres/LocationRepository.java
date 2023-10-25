package com.example.ConnectUs.repository.postgres;

import com.example.ConnectUs.model.postgres.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Integer> {
}
