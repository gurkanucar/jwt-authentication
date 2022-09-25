package com.gucardev.jwtauthentication.service;

import com.gucardev.jwtauthentication.dto.TokenResponseDTO;
import com.gucardev.jwtauthentication.dto.UserDto;
import com.gucardev.jwtauthentication.exception.GeneralException;
import com.gucardev.jwtauthentication.request.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final TokenService tokenService;
    private final UserDetailsServiceImpl userDetailsService;


    public TokenResponseDTO login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (final BadCredentialsException badCredentialsException) {
            throw new GeneralException("Invalid Username or Password", HttpStatus.BAD_REQUEST);
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        return TokenResponseDTO
                .builder()
                .accessToken(tokenService.generateToken(userDetails))
                .user(userService.getUserDto(loginRequest.getUsername()))
                .build();
    }

    public UserDto getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.getUserDto(username);
    }
}
