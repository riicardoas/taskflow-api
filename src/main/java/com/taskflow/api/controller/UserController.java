package com.taskflow.api.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskflow.api.dto.InputNewUserDTO;
import com.taskflow.api.dto.OutputNewUserDTO;
import com.taskflow.api.dto.OutputUserDetailsDTO;
import com.taskflow.api.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@CrossOrigin
@RestController
@RequestMapping("/v1")
@Tag(name = "Users", description = "Consulta e Cadastro de Usuários")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Cadastro de novo Usuário.", tags = {"Users"})
    @PostMapping("/users")
    public ResponseEntity<OutputNewUserDTO> saveNewUser(
            @Valid @RequestBody InputNewUserDTO inputNewUserDTO) throws Exception {
        
        OutputNewUserDTO savedUser = userService.saveNewUser(inputNewUserDTO);
        URI location = URI.create("/v1/users/" + savedUser.getIdNewUser());
        return ResponseEntity.created(location).body(savedUser); 
    }

    @Operation(summary = "Consulta detalhes do Usuário.", tags = {"Users"})
    @GetMapping("/users/{idUser}")
    public ResponseEntity<OutputUserDetailsDTO> findUserById(
            @Parameter(description = "ID do usuário") @PathVariable String idUser) throws Exception {
        
        OutputUserDetailsDTO userDetails = userService.findUserById(idUser);
        return ResponseEntity.ok(userDetails);
    }
}
