package com.gucardev.jwtauthentication.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gucardev.jwtauthentication.service.TokenService;
import com.gucardev.jwtauthentication.service.UserDetailsServiceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final TokenService jwtTokenService;

    private final UserDetailsServiceImpl jwtUserDetailsService;

    public JwtFilter(TokenService jwtTokenService, UserDetailsServiceImpl jwtUserDetailsService) {
        this.jwtTokenService = jwtTokenService;
        this.jwtUserDetailsService = jwtUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = header.substring(7);
        String username;
        try {
            username = jwtTokenService.verifyJWT(token).getSubject();
        } catch (Exception e) {
            sendError(response, e);
            return;
        }
        if (username == null) {
            filterChain.doFilter(request, response);
            return;
        }

        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse res, Exception e) throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        Map<String, String> errors = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        errors.put("error", e.getMessage());
        out.print(mapper.writeValueAsString(errors));
        out.flush();
    }

}