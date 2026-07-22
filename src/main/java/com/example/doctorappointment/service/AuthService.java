package com.example.doctorappointment.service;

import com.example.doctorappointment.dto.request.AuthRequestDTO;
import com.example.doctorappointment.dto.response.AuthResponseDTO;

public interface AuthService {
    AuthResponseDTO register(AuthRequestDTO.RegisterRequest request);
    AuthResponseDTO login(AuthRequestDTO.LoginRequest request);
}
