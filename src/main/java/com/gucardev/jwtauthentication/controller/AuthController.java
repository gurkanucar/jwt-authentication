package com.gucardev.jwtauthentication.controller;

import com.gucardev.jwtauthentication.dto.TokenResponseDTO;
import com.gucardev.jwtauthentication.exception.GeneralException;
import com.gucardev.jwtauthentication.request.LoginRequest;
import com.gucardev.jwtauthentication.service.TokenService;
import com.gucardev.jwtauthentication.service.UserDetailsServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthController(AuthenticationManager authenticationManager,
                          TokenService tokenService,
                          UserDetailsServiceImpl userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public TokenResponseDTO authenticate(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (final BadCredentialsException badCredentialsException) {
            throw new GeneralException("Invalid Username or Password", HttpStatus.BAD_REQUEST);
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        TokenResponseDTO response = new TokenResponseDTO();
        response.setAccessToken(tokenService.generateToken(userDetails));
        return response;
    }


}
