package com.taskflow.api.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.taskflow.api.enums.StatusENUM;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputNewSubTaskDTO {

    @NotBlank(message = "O titulo é obrigatório")
    private String titulo;

    private String descricao;
    private StatusENUM statusEnum;

    @JsonIgnore
    private Date dataConclusao;
    @JsonIgnore
    private Date dataCriacao;
    
}
