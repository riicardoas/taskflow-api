package com.taskflow.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutputUserDetailsDTO {

    private String idUser;
    private String nome;
    private String email;
    
}
 