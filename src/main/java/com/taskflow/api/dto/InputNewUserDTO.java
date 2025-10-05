package com.taskflow.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputNewUserDTO {

    @NotBlank(message = "O nome é obrigatório")
    private String name;

    @NotNull(message = "O e-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    private String email;
    
}
