package com.example.ConnectUs.repository.mongo;

import com.example.ConnectUs.model.mongo.UserMongo;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMongoRepository extends MongoRepository<UserMongo, Integer> {
    List<UserMongo> findByLocationNear(Point p, Distance d);
}
