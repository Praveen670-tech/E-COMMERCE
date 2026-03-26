package com.example.E_COMMERCE.Backend.Controller;

import com.example.E_COMMERCE.Backend.Entity.Token;
import com.example.E_COMMERCE.Backend.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // USER & VENDOR SIGNUP ONLY
    @PostMapping("/signup/{role}")
    public ResponseEntity<?> signup(
            @PathVariable String role,
            @RequestBody Object signupRequest) {
        return ResponseEntity.ok(authService.signup(role, signupRequest));
    }

    // LOGIN → role detection → JWT generation
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password,
            @RequestBody(required = false) java.util.Map<String, Object> body) {

        if ((email == null || password == null) && body != null) {
            Object e = body.get("email");
            Object p = body.get("password");
            email = e == null ? null : e.toString();
            password = p == null ? null : p.toString();
        }
        if (email == null || password == null) {
            return ResponseEntity.badRequest().body("email and password required");
        }
        try {
            Token token = authService.login(email, password);
            return ResponseEntity.ok(token);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(401).body(ex.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<String> me(java.security.Principal principal) {
        return ResponseEntity.ok(principal.getName());
    }
}
