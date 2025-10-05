package com.taskflow.api.repository;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.taskflow.api.entity.SubTaskEntity;
import com.taskflow.api.enums.StatusENUM;


public interface SubTaskRepository extends MongoRepository<SubTaskEntity, ObjectId>  {

    Page<SubTaskEntity> findByTarefaId(String tarefaId, Pageable pageable);

    boolean existsByTarefaIdAndStatusEnumNot(String tarefaId, StatusENUM statusEnum);

}
