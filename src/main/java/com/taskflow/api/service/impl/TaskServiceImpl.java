package com.taskflow.api.service.impl;

import java.util.Date;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import com.taskflow.api.dto.InputNewTaskDTO;
import com.taskflow.api.dto.OutputNewTaskDTO;
import com.taskflow.api.dto.OutputTaskListDTO;
import com.taskflow.api.entity.TaskEntity;
import com.taskflow.api.enums.StatusENUM;
import com.taskflow.api.repository.SubTaskRepository;
import com.taskflow.api.repository.TaskRepository;
import com.taskflow.api.repository.UserRepository;
import com.taskflow.api.service.TaskService;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubTaskRepository subTaskRepository;

    @Override
    public OutputNewTaskDTO createTask(InputNewTaskDTO inputNewUserDTO) throws Exception {     

        if(inputNewUserDTO.getStatusEnum() == StatusENUM.CONCLUIDA) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        String uid = inputNewUserDTO.getUsuarioId();
        if (uid != null) {
            if (!ObjectId.isValid(uid)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST); 
            }
            if (!userRepository.existsById(new ObjectId(uid))) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY); 
            }
        }
        
        TaskEntity entity =  TaskEntity.builder()
            .titulo(inputNewUserDTO.getTitulo().trim())
            .descricao(inputNewUserDTO.getDescricao())
            .statusEnum(inputNewUserDTO.getStatusEnum())
            .dataCriacao(new Date())
            .dataConclusao(null)
            .usuarioId(inputNewUserDTO.getUsuarioId())
            .build(); 
 
        TaskEntity task = taskRepository.save(entity);
        return new OutputNewTaskDTO(task.getId().toString());
    }

    @Override
    public Page<OutputTaskListDTO> listTasks(StatusENUM status, String usuarioId, Pageable pageable) {
        TaskEntity probe = new TaskEntity();
        probe.setStatusEnum(status);
        probe.setUsuarioId(StringUtils.hasText(usuarioId) ? usuarioId : null);
 
        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withIgnoreNullValues(); 

        Page<TaskEntity> page = taskRepository.findAll(Example.of(probe, matcher), pageable);

        return page.map(task -> {
            OutputTaskListDTO dto = new OutputTaskListDTO();
            dto.setId(task.getId().toHexString());
            dto.setTitulo(task.getTitulo());
            dto.setDescricao(task.getDescricao());
            dto.setStatusEnum(task.getStatusEnum());
            dto.setDataCriacao(task.getDataCriacao());
            dto.setDataConclusao(task.getDataConclusao());
            dto.setUsuarioId(task.getUsuarioId());
            return dto;
        });
    }

    @Override
    public void updateTaskStatus(String idTask, String novoStatus) throws Exception {
        TaskEntity task = taskRepository.findById(new ObjectId(idTask))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        StatusENUM statusEnum = StatusENUM.valueOf(novoStatus.trim().toUpperCase());
 
        boolean concluida = statusEnum == StatusENUM.CONCLUIDA;
        boolean pendentes = concluida && subTaskRepository.existsByTarefaIdAndStatusEnumNot(idTask, StatusENUM.CONCLUIDA);

        if (pendentes) throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);

        task.setStatusEnum(statusEnum);
        task.setDataConclusao(concluida ? new Date() : null);

        taskRepository.save(task);
    }
}
