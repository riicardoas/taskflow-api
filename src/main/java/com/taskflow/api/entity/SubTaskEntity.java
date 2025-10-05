package com.taskflow.api.entity;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.taskflow.api.enums.StatusENUM;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "subtasks")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubTaskEntity {

    @Id
    private ObjectId id;

    private String titulo;
    private String descricao;
    private StatusENUM statusEnum;
    private Date dataCriacao;
    private Date dataConclusao;
    private String tarefaId;
}
