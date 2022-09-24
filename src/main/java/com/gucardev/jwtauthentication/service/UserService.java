package com.gucardev.jwtauthentication.service;


import com.gucardev.jwtauthentication.dto.UserDto;
import com.gucardev.jwtauthentication.exception.GeneralException;
import com.gucardev.jwtauthentication.model.User;
import com.gucardev.jwtauthentication.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public User create(User user) {
        return userRepository.save(user);
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException("user not found!", HttpStatus.NOT_FOUND));
    }

    public UserDto getUserDto(String username) {
        var user = findUserByUsername(username);
        return UserDto.builder()
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

}
