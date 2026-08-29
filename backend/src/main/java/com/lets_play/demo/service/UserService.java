package com.lets_play.demo.service;

import com.lets_play.demo.domain.entity.Role;
import com.lets_play.demo.domain.entity.User;
import com.lets_play.demo.domain.repository.ProductRepository;
import com.lets_play.demo.domain.repository.UserRepository;
import com.lets_play.demo.dto.request.CreateUserRequest;
import com.lets_play.demo.dto.request.UpdateUserRequest;
import com.lets_play.demo.dto.response.UserResponse;
import com.lets_play.demo.mapper.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, ProductRepository productRepository,
                       PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toResponse).toList();
    }

    public UserResponse getUserById(String id) {
        return userMapper.toResponse(findUser(id));
    }

    public UserResponse createUser(CreateUserRequest request) {
        ensureEmailAvailable(request.email(), null);
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role() != null ? request.role() : Role.ROLE_USER);
        return userMapper.toResponse(userRepository.save(user));
    }

    public UserResponse updateUser(String id, UpdateUserRequest request) {
        User user = findUser(id);
        if (request.email() != null && !request.email().equals(user.getEmail())) {
            ensureEmailAvailable(request.email(), id);
            user.setEmail(request.email());
        }
        if (request.name() != null) user.setName(request.name());
        if (request.password() != null) user.setPassword(passwordEncoder.encode(request.password()));
        
        if (user.getRole() == Role.ROLE_ADMIN && userRepository.countByRole(Role.ROLE_ADMIN) <= 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot change the role of the only admin user");
        }
        if (request.role() != null) user.setRole(request.role());
        return userMapper.toResponse(userRepository.save(user));
    }

    public void deleteUser(String id) {
        User user = findUser(id);

        if (user.getRole() == Role.ROLE_ADMIN && userRepository.countByRole(Role.ROLE_ADMIN) <= 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot delete the only admin user");
        }

        productRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }

    private User findUser(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private void ensureEmailAvailable(String email, String excludedId) {
        userRepository.findByEmail(email).ifPresent(existing -> {
            if (!existing.getId().equals(excludedId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
            }
        });
    }
}