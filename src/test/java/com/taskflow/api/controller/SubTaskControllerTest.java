package com.taskflow.api.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import com.taskflow.api.service.SubTaskService;

@WebMvcTest(SubTaskController.class)
class SubTaskControllerTest {

    @Autowired private MockMvc mvc;

    @MockBean private SubTaskService subTaskService;

    @Test
    void patchStatus_shouldReturn204_onSuccess() throws Exception {
        doNothing().when(subTaskService).updateSubTaskStatus("123", "CONCLUIDA");

        mvc.perform(patch("/v1/subtasks/123/status")
                .param("status", "CONCLUIDA"))
           .andExpect(status().isNoContent());
    }

    @Test
    void patchStatus_shouldPropagateNotFound() throws Exception {
        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND))
                .when(subTaskService).updateSubTaskStatus("999", "CONCLUIDA");

        mvc.perform(patch("/v1/subtasks/999/status")
                .param("status", "CONCLUIDA"))
           .andExpect(status().isNotFound());
    }
}