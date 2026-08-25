package com.lets_play.demo.service;

import com.lets_play.demo.domain.entity.Role;
import com.lets_play.demo.domain.entity.User;
import com.lets_play.demo.domain.repository.ProductRepository;
import com.lets_play.demo.domain.repository.UserRepository;
import com.lets_play.demo.dto.request.CreateUserRequest;
import com.lets_play.demo.dto.request.UpdateUserRequest;
import com.lets_play.demo.dto.response.UserResponse;
import com.lets_play.demo.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Test
    void createUserHashesPasswordAndDefaultsToUserRole() {
        UserRepository userRepository = mock(UserRepository.class);
        ProductRepository productRepository = mock(ProductRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        UserMapper userMapper = mock(UserMapper.class);
        UserService userService = new UserService(userRepository, productRepository, passwordEncoder, userMapper);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toResponse(any(User.class))).thenReturn(
                new UserResponse("1", "User", "user@example.com", Role.ROLE_USER, null));

        UserResponse response = userService.createUser(
                new CreateUserRequest("User", "user@example.com", "password", null));

        assertEquals(Role.ROLE_USER, response.role());
        verify(passwordEncoder).encode("password");
    }

    @Test
    void deleteUserRemovesOwnedProductsBeforeUser() {
        UserRepository userRepository = mock(UserRepository.class);
        ProductRepository productRepository = mock(ProductRepository.class);
        UserService userService = new UserService(
                userRepository, productRepository, mock(PasswordEncoder.class), mock(UserMapper.class));
        User user = User.builder().id("user-1").email("user@example.com").build();
        when(userRepository.findById("user-1")).thenReturn(Optional.of(user));

        userService.deleteUser("user-1");

        verify(productRepository).deleteByUserId("user-1");
        verify(userRepository).deleteById("user-1");
    }

    @Test
    void updateUserRejectsDuplicateEmail() {
        UserRepository userRepository = mock(UserRepository.class);
        User existing = User.builder().id("user-1").email("old@example.com").build();
        User duplicate = User.builder().id("user-2").email("new@example.com").build();
        when(userRepository.findById("user-1")).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.of(duplicate));
        UserService userService = new UserService(
                userRepository, mock(ProductRepository.class), mock(PasswordEncoder.class), mock(UserMapper.class));

        assertThrows(ResponseStatusException.class, () -> userService.updateUser(
                "user-1", new UpdateUserRequest(null, "new@example.com", null, null)));
    }
}
