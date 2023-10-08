package com.example.ConnectUs.repository.postgres;

import com.example.ConnectUs.model.postgres.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PageRepository extends JpaRepository<Page, Integer> {

    Optional<Page> findById(Integer id);
}
