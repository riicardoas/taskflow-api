package com.taskflow.api.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taskflow.api.dto.InputNewTaskDTO;
import com.taskflow.api.dto.OutputNewTaskDTO;
import com.taskflow.api.dto.OutputTaskListDTO;
import com.taskflow.api.enums.StatusENUM;
import com.taskflow.api.service.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@CrossOrigin
@RestController
@RequestMapping("/v1")
@Tag(name = "Tasks", description = "Criação, consulta e atualização de tarefas.")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Criação de nova tarefa.", tags = {"Tasks"})
    @PostMapping("/tasks")
    public ResponseEntity<OutputNewTaskDTO> createTask(
            @Valid @RequestBody InputNewTaskDTO inputNewTaskDTO) throws Exception {
        
        OutputNewTaskDTO savedTask = taskService.createTask(inputNewTaskDTO);
        URI location = URI.create("/v1/tasks/" + savedTask.getIdTask());
        return ResponseEntity.created(location).body(savedTask); 
    }


    @Operation(summary = "Lista de tarefas paginada com filtros opcionais.", tags = {"Tasks"})
    @GetMapping("/tasks")
    public ResponseEntity<Page<OutputTaskListDTO>> listTasks(
            @RequestParam(required = false) StatusENUM status,
            @RequestParam(required = false) String usuarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCriacao"));
        Page<OutputTaskListDTO> result = taskService.listTasks(status, usuarioId, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Atualiza o status de uma tarefa.", tags = {"Tasks"})
    @PatchMapping("/tasks/{idTask}/status")
    public ResponseEntity<Void> updateTaskStatus(
            @PathVariable String idTask,
            @Parameter(
                description = "Novo status da tarefa",
                schema = @Schema(implementation = StatusENUM.class),
                required = true
            )
            @RequestParam("status") String novoStatus) throws Exception {

        taskService.updateTaskStatus(idTask, novoStatus);
        return ResponseEntity.noContent().build();
    }

        
}
