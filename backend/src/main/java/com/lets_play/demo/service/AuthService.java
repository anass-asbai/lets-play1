package com.lets_play.demo.service;

import com.lets_play.demo.domain.entity.Role;
import com.lets_play.demo.domain.entity.User;
import com.lets_play.demo.domain.repository.UserRepository;
import com.lets_play.demo.dto.request.LoginRequest;
import com.lets_play.demo.dto.request.RegisterRequest;
import com.lets_play.demo.dto.response.AuthResponse;
import com.lets_play.demo.mapper.UserMapper;
import com.lets_play.demo.security.jwt.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            AuthenticationManager authenticationManager,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already in use");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role() != null ? request.role() : Role.ROLE_USER);

        User savedUser = userRepository.save(user);

        var userDetails = org.springframework.security.core.userdetails.User
                .withUsername(savedUser.getEmail())
                .password(savedUser.getPassword())
                .authorities(savedUser.getRole().name())
                .build();

        String jwtToken = jwtTokenProvider.generateToken(userDetails);

        return new AuthResponse(jwtToken, userMapper.toResponse(savedUser));
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();

        String jwtToken = jwtTokenProvider.generateToken(userDetails);

        return new AuthResponse(jwtToken, userMapper.toResponse(user));
    }
}