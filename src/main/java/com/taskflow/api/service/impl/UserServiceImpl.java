package com.taskflow.api.service.impl;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.taskflow.api.dto.InputNewUserDTO;
import com.taskflow.api.dto.OutputNewUserDTO;
import com.taskflow.api.dto.OutputUserDetailsDTO;
import com.taskflow.api.entity.UserEntity;
import com.taskflow.api.repository.UserRepository;
import com.taskflow.api.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OutputNewUserDTO saveNewUser(InputNewUserDTO inputNewUserDTO) throws Exception {     

        if (userRepository.existsByEmail(inputNewUserDTO.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        UserEntity entity = UserEntity.builder()
                .name(inputNewUserDTO.getName())
                .email(inputNewUserDTO.getEmail())
                .build(); 
 
        UserEntity userSaved = userRepository.save(entity);
        return new OutputNewUserDTO(userSaved.getIdUser().toString());
    }

    @Override
    public OutputUserDetailsDTO findUserById(String idUser) {
        if (!ObjectId.isValid(idUser)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST); 

        return userRepository.findById(new ObjectId(idUser))
            .map(user -> new OutputUserDetailsDTO(user.getIdUser().toString(), user.getName(), user.getEmail()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)); 
    }
}
