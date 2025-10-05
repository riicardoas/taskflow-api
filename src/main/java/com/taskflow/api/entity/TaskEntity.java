package com.taskflow.api.entity;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.taskflow.api.enums.StatusENUM;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "tasks")
@CompoundIndex(name = "idx_task_status_usuario", def = "{'statusEnum': 1, 'usuarioId': 1}")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity {

    @Id
    private ObjectId id;

    @Indexed(name = "idx_task_status")
    private StatusENUM statusEnum;

    @Indexed(name = "idx_task_usuario")
    private String usuarioId;

    private String titulo;
    private String descricao;
    private Date dataCriacao;
    private Date dataConclusao;
}
