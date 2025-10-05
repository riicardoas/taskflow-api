package com.taskflow.api.service;

import org.springframework.stereotype.Service;

import com.taskflow.api.dto.InputNewUserDTO;
import com.taskflow.api.dto.OutputNewUserDTO;
import com.taskflow.api.dto.OutputUserDetailsDTO;

@Service
public interface UserService {

    OutputNewUserDTO saveNewUser(InputNewUserDTO inputNewUserDTO) throws Exception;

    OutputUserDetailsDTO findUserById(String idUser) throws Exception;

    
}
