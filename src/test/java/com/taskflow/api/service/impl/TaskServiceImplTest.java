package com.taskflow.api.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.taskflow.api.entity.TaskEntity;
import com.taskflow.api.enums.StatusENUM;
import com.taskflow.api.repository.SubTaskRepository;
import com.taskflow.api.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock private TaskRepository taskRepository;
    @Mock private SubTaskRepository subTaskRepository;

    @InjectMocks private TaskServiceImpl service;

    private String taskId;
    private ObjectId taskObjectId;

    @BeforeEach
    void setup() {
        taskId = new ObjectId().toHexString();
        taskObjectId = new ObjectId(taskId);
    }

    @Test
    void updateTaskStatus_shouldThrow404_whenTaskNotFound() {
        when(taskRepository.findById(taskObjectId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                service.updateTaskStatus(taskId, "PENDENTE"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void updateTaskStatus_shouldThrow422_whenConcludingWithPendingSubtasks() {
        TaskEntity task = new TaskEntity();
        when(taskRepository.findById(taskObjectId)).thenReturn(Optional.of(task));
        when(subTaskRepository.existsByTarefaIdAndStatusEnumNot(taskId, StatusENUM.CONCLUIDA)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                service.updateTaskStatus(taskId, "CONCLUIDA"));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ex.getStatusCode());
        verify(taskRepository, never()).save(any());
    }

    @Test
    void updateTaskStatus_shouldSetConclusionDate_whenConcludedWithoutPendingSubtasks() throws Exception {
        TaskEntity task = new TaskEntity();
        when(taskRepository.findById(taskObjectId)).thenReturn(Optional.of(task));
        when(subTaskRepository.existsByTarefaIdAndStatusEnumNot(taskId, StatusENUM.CONCLUIDA)).thenReturn(false);

        service.updateTaskStatus(taskId, "CONCLUIDA");

        assertEquals(StatusENUM.CONCLUIDA, task.getStatusEnum());
        assertNotNull(task.getDataConclusao());
        verify(taskRepository).save(task);
    }

    @Test
    void updateTaskStatus_shouldClearConclusionDate_whenNotConcluded() throws Exception {
        TaskEntity task = new TaskEntity();
        task.setDataConclusao(new Date());
        when(taskRepository.findById(taskObjectId)).thenReturn(Optional.of(task));

        service.updateTaskStatus(taskId, "PENDENTE");

        assertEquals(StatusENUM.PENDENTE, task.getStatusEnum());
        assertNull(task.getDataConclusao());
        verify(taskRepository).save(task);
    }
}