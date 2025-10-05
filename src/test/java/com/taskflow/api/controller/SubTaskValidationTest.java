package com.taskflow.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.taskflow.api.service.SubTaskService;

@WebMvcTest(SubTaskController.class)
class SubTaskValidationTest {

    @Autowired private MockMvc mvc;
    @MockBean private SubTaskService subTaskService;

    @Test
    void postSubTask_shouldReturn400_whenMissingRequiredFields() throws Exception {
        String body = "{ \"titulo\": \"\", \"descricao\": \"x\", \"statusEnum\": \"PENDENTE\" }";

        mvc.perform(post("/v1/tasks/aaaaaaaaaaaaaaaaaaaaaaaa/subtasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
           .andExpect(status().isBadRequest());
    }
}