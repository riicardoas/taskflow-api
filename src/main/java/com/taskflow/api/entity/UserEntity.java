package com.taskflow.api.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "users")
@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class UserEntity {
    @Id
    private ObjectId idUser; 

    private String name;

    @Indexed(unique = true) 
    private String email;
}
