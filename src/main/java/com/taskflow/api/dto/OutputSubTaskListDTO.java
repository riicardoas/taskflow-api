package com.taskflow.api.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taskflow.api.enums.StatusENUM;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutputSubTaskListDTO {

    private String id;
    private String titulo;
    private String descricao;
    private StatusENUM statusEnum;
    private String tarefaId;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    private Date dataCriacao;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    private Date dataConclusao;
    
}
