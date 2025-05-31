package com.mongo.lostfound.repository;

import com.mongo.lostfound.entity.Item;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ItemRepository extends MongoRepository<Item, String> {
    void deleteByUserId(String userId);

}
