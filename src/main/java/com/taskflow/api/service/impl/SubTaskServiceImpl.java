package com.taskflow.api.service.impl;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.taskflow.api.dto.InputNewSubTaskDTO;
import com.taskflow.api.dto.OutputNewSubTaskDTO;
import com.taskflow.api.dto.OutputSubTaskListDTO;
import com.taskflow.api.entity.SubTaskEntity;
import com.taskflow.api.enums.StatusENUM;
import com.taskflow.api.repository.SubTaskRepository;
import com.taskflow.api.repository.TaskRepository;
import com.taskflow.api.service.SubTaskService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubTaskServiceImpl implements SubTaskService {

    private final TaskRepository taskRepository;
    private final SubTaskRepository subTaskRepository;

    @Override
    public OutputNewSubTaskDTO createSubTask(String taskId, InputNewSubTaskDTO inputNewSubTaskDTO) throws Exception {
        taskRepository.findById(new ObjectId(taskId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (inputNewSubTaskDTO.getStatusEnum() == StatusENUM.CONCLUIDA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        SubTaskEntity subTask = SubTaskEntity.builder()
                .titulo(inputNewSubTaskDTO.getTitulo())
                .descricao(inputNewSubTaskDTO.getDescricao())
                .statusEnum(inputNewSubTaskDTO.getStatusEnum())
                .dataCriacao(new Date())
                .dataConclusao(null)
                .tarefaId(taskId)
                .build();

        SubTaskEntity savedSubTask = subTaskRepository.save(subTask);

        OutputNewSubTaskDTO output = new OutputNewSubTaskDTO();
        output.setIdSubTask(savedSubTask.getId().toString());
        return output;
    }

    @Override
    public Page<OutputSubTaskListDTO> listSubTasksByTask(String taskId, Pageable pageable) throws Exception {
        taskRepository.findById(new ObjectId(taskId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Page<SubTaskEntity> page = subTaskRepository.findByTarefaId(taskId, pageable);

        return page.map(subtask -> {
            OutputSubTaskListDTO dto = new OutputSubTaskListDTO();
            dto.setId(subtask.getId().toString());
            dto.setTitulo(subtask.getTitulo());
            dto.setDescricao(subtask.getDescricao());
            dto.setStatusEnum(subtask.getStatusEnum());
            dto.setTarefaId(subtask.getTarefaId());
            dto.setDataCriacao(subtask.getDataCriacao());
            dto.setDataConclusao(subtask.getDataConclusao());
            return dto;
        });
    }

    @Override
    public void updateSubTaskStatus(String idSubTask, String novoStatus) throws Exception {
        SubTaskEntity subTask = subTaskRepository.findById(new ObjectId(idSubTask))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND));

        StatusENUM statusEnum;
        statusEnum = StatusENUM.valueOf(novoStatus.trim().toUpperCase());
        subTask.setStatusEnum(statusEnum);
        subTask.setDataConclusao(statusEnum == StatusENUM.CONCLUIDA ? new Date() : null);
        subTaskRepository.save(subTask);
    }
}
