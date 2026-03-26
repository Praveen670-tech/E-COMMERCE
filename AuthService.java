package com.example.E_COMMERCE.Backend.Service;

import com.example.E_COMMERCE.Backend.Entity.Token;

public interface AuthService {

    Object signup(String role, Object request);

    Token login(String email, String password);
}
