package com.taskflow.api.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.taskflow.api.entity.TaskEntity;

public interface TaskRepository extends MongoRepository<TaskEntity, ObjectId>  {

}
