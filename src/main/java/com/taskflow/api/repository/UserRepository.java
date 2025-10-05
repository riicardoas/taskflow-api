package com.taskflow.api.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.taskflow.api.entity.UserEntity;

public interface UserRepository extends MongoRepository<UserEntity, ObjectId> {
    boolean existsByEmail(String email);
    
}
