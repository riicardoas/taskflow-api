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

import com.taskflow.api.dto.InputNewSubTaskDTO;
import com.taskflow.api.dto.OutputNewSubTaskDTO;
import com.taskflow.api.dto.OutputSubTaskListDTO;
import com.taskflow.api.enums.StatusENUM;
import com.taskflow.api.service.SubTaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@CrossOrigin
@RestController
@RequestMapping("/v1")
@Tag(name = "Subtasks", description = "Criação, consulta e atualização de sub-tarefas.")
@RequiredArgsConstructor
public class SubTaskController {

    private final SubTaskService subTaskService;

    @Operation(summary = "Criação de nova sub-tarefa vinculada a uma tarefa.", tags = {"Subtasks"})
    @PostMapping("/tasks/{taskId}/subtasks")
    public ResponseEntity<OutputNewSubTaskDTO> createSubTask(
            @PathVariable String taskId,
            @Valid @RequestBody InputNewSubTaskDTO inputNewSubTaskDTO) throws Exception {

        OutputNewSubTaskDTO savedSubTask = subTaskService.createSubTask(taskId, inputNewSubTaskDTO);
        URI location = URI.create("/v1/tasks/" + taskId + "/subtasks/" + savedSubTask.getIdSubTask());
        return ResponseEntity.created(location).body(savedSubTask);
    }

     @Operation(summary = "Lista paginada de subtarefas de uma tarefa.", tags = {"Subtasks"})
    @GetMapping("/tasks/{taskId}/subtasks")
    public ResponseEntity<Page<OutputSubTaskListDTO>> listSubTasksByTask(
            @PathVariable String taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws Exception {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCriacao"));
        Page<OutputSubTaskListDTO> result = subTaskService.listSubTasksByTask(taskId, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Atualiza o status de uma subtarefa.", tags = {"Subtasks"})
    @PatchMapping("/subtasks/{idSubTask}/status")
    public ResponseEntity<Void> updateSubTaskStatus(
        @PathVariable String idSubTask,
        @Parameter(description = "Novo status da subtarefa", schema = @Schema(implementation = StatusENUM.class),required = true)
        @RequestParam("status") String novoStatus) throws Exception {
        subTaskService.updateSubTaskStatus(idSubTask, novoStatus);
        return ResponseEntity.noContent().build();
    }
}
