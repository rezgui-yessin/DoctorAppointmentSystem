package com.example.doctorappointment.service.impl;

import com.example.doctorappointment.dto.request.AuthRequestDTO;
import com.example.doctorappointment.dto.response.AuthResponseDTO;
import com.example.doctorappointment.entity.User;
import com.example.doctorappointment.entity.enums.Role;
import com.example.doctorappointment.exception.DuplicateResourceException;
import com.example.doctorappointment.repository.UserRepository;
import com.example.doctorappointment.security.JwtUtil;
import com.example.doctorappointment.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public AuthResponseDTO register(AuthRequestDTO.RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("An account with this email already exists");
        }

        Role role = Role.valueOf(request.role().toUpperCase());

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(role)
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), Map.of("role", role.name()));
        return new AuthResponseDTO(token, user.getEmail(), role.name());
    }

    @Override
    public AuthResponseDTO login(AuthRequestDTO.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("User not found after authentication"));

        String token = jwtUtil.generateToken(user.getEmail(), Map.of("role", user.getRole().name()));
        return new AuthResponseDTO(token, user.getEmail(), user.getRole().name());
    }
}
