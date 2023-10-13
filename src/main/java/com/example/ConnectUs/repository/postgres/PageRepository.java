package com.example.ConnectUs.repository.postgres;

import com.example.ConnectUs.model.postgres.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PageRepository extends JpaRepository<Page, Integer> {

    Optional<Page> findById(Integer id);

    @Query("SELECT p FROM Page p WHERE p.name ILIKE %:searchText%")
    List<Page> findPagesBySearchText(@Param("searchText") String searchText);
}
