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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.taskflow.api.dto.InputNewSubTaskDTO;
import com.taskflow.api.dto.OutputSubTaskListDTO;
import com.taskflow.api.entity.SubTaskEntity;
import com.taskflow.api.entity.TaskEntity;
import com.taskflow.api.enums.StatusENUM;
import com.taskflow.api.repository.SubTaskRepository;
import com.taskflow.api.repository.TaskRepository;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class SubTaskServiceImplTest {

    @Mock private TaskRepository taskRepository;
    @Mock private SubTaskRepository subTaskRepository;

    @InjectMocks private SubTaskServiceImpl service;

    private String taskId;
    private ObjectId taskObjectId;

    @BeforeEach
    void setup() {
        taskId = new ObjectId().toHexString();
        taskObjectId = new ObjectId(taskId);
    }

    @Test
    void createSubTask_shouldThrow404_whenTaskNotFound() {
        when(taskRepository.findById(taskObjectId)).thenReturn(Optional.empty());

        InputNewSubTaskDTO dto = new InputNewSubTaskDTO();
        dto.setTitulo("Sub 1");
        dto.setDescricao("desc");
        dto.setStatusEnum(StatusENUM.PENDENTE);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                service.createSubTask(taskId, dto));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verifyNoInteractions(subTaskRepository);
    }

    @Test
    void createSubTask_shouldThrow400_whenStatusIsConcluida() {
        when(taskRepository.findById(taskObjectId)).thenReturn(Optional.of(new TaskEntity()));

        InputNewSubTaskDTO dto = new InputNewSubTaskDTO();
        dto.setTitulo("Sub 1");
        dto.setDescricao("desc");
        dto.setStatusEnum(StatusENUM.CONCLUIDA);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                service.createSubTask(taskId, dto));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(subTaskRepository, never()).save(any());
    }

    @Test
    void createSubTask_shouldPersistAndReturnId_onSuccess() throws Exception {
        when(taskRepository.findById(taskObjectId)).thenReturn(Optional.of(new TaskEntity()));

        InputNewSubTaskDTO dto = new InputNewSubTaskDTO();
        dto.setTitulo("Sub 1");
        dto.setDescricao("desc");
        dto.setStatusEnum(StatusENUM.PENDENTE);

        SubTaskEntity saved = SubTaskEntity.builder()
                .id(new ObjectId())
                .titulo(dto.getTitulo())
                .descricao(dto.getDescricao())
                .statusEnum(dto.getStatusEnum())
                .dataCriacao(new Date())
                .tarefaId(taskId)
                .build();

        when(subTaskRepository.save(any(SubTaskEntity.class))).thenReturn(saved);

        var out = service.createSubTask(taskId, dto);

        assertNotNull(out);
        assertEquals(saved.getId().toHexString(), out.getIdSubTask());
        verify(subTaskRepository).save(any(SubTaskEntity.class));
    }

    @Test
    void listSubTasksByTask_shouldThrow404_whenTaskNotFound() {
        when(taskRepository.findById(taskObjectId)).thenReturn(Optional.empty());
        Pageable pageable = PageRequest.of(0, 10);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                service.listSubTasksByTask(taskId, pageable));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void listSubTasksByTask_shouldMapEntitiesToDTOs() throws Exception {
        when(taskRepository.findById(taskObjectId)).thenReturn(Optional.of(new TaskEntity()));

        SubTaskEntity s1 = SubTaskEntity.builder()
                .id(new ObjectId())
                .titulo("a")
                .descricao("a")
                .statusEnum(StatusENUM.PENDENTE)
                .tarefaId(taskId)
                .dataCriacao(new Date())
                .build();
        Page<SubTaskEntity> page = new PageImpl<>(List.of(s1));
        when(subTaskRepository.findByTarefaId(eq(taskId), any(Pageable.class))).thenReturn(page);

        Pageable pageable = PageRequest.of(0, 10);
        Page<OutputSubTaskListDTO> result = service.listSubTasksByTask(taskId, pageable);

        assertEquals(1, result.getTotalElements());
        OutputSubTaskListDTO dto = result.getContent().get(0);
        assertEquals(s1.getId().toHexString(), dto.getId());
        assertEquals("A", dto.getTitulo());
        assertEquals(StatusENUM.PENDENTE, dto.getStatusEnum());
    }

    @Test
    void updateSubTaskStatus_shouldSetConclusionDate_whenConcluded() throws Exception {
        ObjectId subId = new ObjectId();
        SubTaskEntity sub = SubTaskEntity.builder()
                .id(subId)
                .statusEnum(StatusENUM.PENDENTE)
                .build();

        when(subTaskRepository.findById(subId)).thenReturn(Optional.of(sub));
        when(subTaskRepository.save(any(SubTaskEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        service.updateSubTaskStatus(subId.toHexString(), "CONCLUIDA");

        assertEquals(StatusENUM.CONCLUIDA, sub.getStatusEnum());
        assertNotNull(sub.getDataConclusao());
    }

    @Test
    void updateSubTaskStatus_shouldClearConclusionDate_whenBackToPending() throws Exception {
        ObjectId subId = new ObjectId();
        SubTaskEntity sub = SubTaskEntity.builder()
                .id(subId)
                .statusEnum(StatusENUM.CONCLUIDA)
                .dataConclusao(new Date())
                .build();

        when(subTaskRepository.findById(subId)).thenReturn(Optional.of(sub));
        when(subTaskRepository.save(any(SubTaskEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        service.updateSubTaskStatus(subId.toHexString(), "PENDENTE");

        assertEquals(StatusENUM.PENDENTE, sub.getStatusEnum());
        assertNull(sub.getDataConclusao());
    }
}