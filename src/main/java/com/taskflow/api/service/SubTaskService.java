package com.taskflow.api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.taskflow.api.dto.InputNewSubTaskDTO;
import com.taskflow.api.dto.OutputNewSubTaskDTO;
import com.taskflow.api.dto.OutputSubTaskListDTO;

@Service
public interface SubTaskService {

    OutputNewSubTaskDTO createSubTask(String taskId, InputNewSubTaskDTO inputNewSubTaskDTO) throws Exception;

    Page<OutputSubTaskListDTO> listSubTasksByTask(String taskId, Pageable pageable) throws Exception;

    void updateSubTaskStatus(String idSubTask, String novoStatus) throws Exception;
    
}
