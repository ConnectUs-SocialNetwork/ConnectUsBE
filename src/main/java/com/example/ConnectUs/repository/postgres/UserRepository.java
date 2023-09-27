package com.example.ConnectUs.repository.postgres;

import java.util.List;
import java.util.Optional;

import com.example.ConnectUs.model.postgres.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
    Optional<User> findById(Integer id);

    @Query("SELECT u FROM User u WHERE u.firstname LIKE %?1% OR u.lastname LIKE %?1%")
    List<User> findFriendsAndNonFriendsBySearchText(@Param("searchText") String searchText, @Param("userId") Integer userId);

}
