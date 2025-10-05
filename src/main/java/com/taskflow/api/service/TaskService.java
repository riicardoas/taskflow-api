package com.taskflow.api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.taskflow.api.dto.InputNewTaskDTO;
import com.taskflow.api.dto.OutputNewTaskDTO;
import com.taskflow.api.dto.OutputTaskListDTO;
import com.taskflow.api.enums.StatusENUM;

@Service
public interface TaskService {

    OutputNewTaskDTO createTask(InputNewTaskDTO inputNewTaskDTO) throws Exception;

    Page<OutputTaskListDTO> listTasks(StatusENUM status, String usuarioId, Pageable pageable);

    void updateTaskStatus(String idTask, String novoStatus) throws Exception;


    
}
