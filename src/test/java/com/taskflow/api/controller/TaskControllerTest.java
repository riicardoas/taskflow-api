package com.taskflow.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import com.taskflow.api.service.TaskService;

import static org.mockito.Mockito.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired private MockMvc mvc;

    @MockBean private TaskService taskService;

    @Test
    void patchStatus_shouldReturn204_onSuccess() throws Exception {
        doNothing().when(taskService).updateTaskStatus("456", "PENDENTE");

        mvc.perform(patch("/v1/tasks/456/status").param("status", "PENDENTE"))
           .andExpect(status().isNoContent());
    }

    @Test
    void patchStatus_shouldPropagate422() throws Exception {
        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY))
                .when(taskService).updateTaskStatus("456", "CONCLUIDA");

        mvc.perform(patch("/v1/tasks/456/status").param("status", "CONCLUIDA"))
           .andExpect(status().isUnprocessableEntity());
    }
}