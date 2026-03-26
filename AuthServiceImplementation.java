package com.example.E_COMMERCE.Backend.ServiceImplementation;

import com.example.E_COMMERCE.Backend.Entity.Admin;
import com.example.E_COMMERCE.Backend.Entity.Token;
import com.example.E_COMMERCE.Backend.Entity.User;
import com.example.E_COMMERCE.Backend.Entity.UserRole;
import com.example.E_COMMERCE.Backend.Entity.Vendor;
import com.example.E_COMMERCE.Backend.Repository.AdminRepository;
import com.example.E_COMMERCE.Backend.Repository.TokenRepository;
import com.example.E_COMMERCE.Backend.Repository.UserRepository;
import com.example.E_COMMERCE.Backend.Repository.UserRoleRepository;
import com.example.E_COMMERCE.Backend.Repository.VendorRepository;
import com.example.E_COMMERCE.Backend.Security.JwtUtil;
import com.example.E_COMMERCE.Backend.Service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImplementation implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    // =========================
    // SIGNUP (USER / VENDOR)
    // =========================
    @Override
    public Object signup(String role, Object request) {

        if (role.equalsIgnoreCase("USER")) {
            User user = objectMapper.convertValue(request, User.class);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);

            UserRole userRole = new UserRole();
            userRole.setEmail(user.getEmail());
            userRole.setRoleName("USER");
            userRoleRepository.save(userRole);

            return user;
        }

        if (role.equalsIgnoreCase("VENDOR")) {
            Vendor vendor = objectMapper.convertValue(request, Vendor.class);
            vendor.setPassword(passwordEncoder.encode(vendor.getPassword()));
            vendorRepository.save(vendor);

            UserRole userRole = new UserRole();
            userRole.setEmail(vendor.getEmail());
            userRole.setRoleName("VENDOR");
            userRoleRepository.save(userRole);

            return vendor;
        }

        if (role.equalsIgnoreCase("ADMIN")) {
            Admin admin = objectMapper.convertValue(request, Admin.class);
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
            adminRepository.save(admin);

            UserRole userRole = new UserRole();
            userRole.setEmail(admin.getEmail());
            userRole.setRoleName("ADMIN");
            userRoleRepository.save(userRole);

            return admin;
        }

        throw new RuntimeException("Signup not supported for role: " + role);
    }

    // =========================
    // LOGIN
    // =========================
    @Override
    public Token login(String email, String password) {

        // 🔹 STEP 1: Fetch role using email
        UserRole userRole = userRoleRepository.findByEmail(email);

        if (userRole == null) {
            throw new RuntimeException("Role not found for email: " + email);
        }

        String role = userRole.getRoleName();
        String encodedPassword = null;

        // 🔹 STEP 2: Fetch password from correct table
        if (role.equals("USER")) {
            User user = userRepository.findByEmail(email);
            if (user != null && passwordEncoder.matches(password, user.getPassword())) {
                user.setLastLogin(java.time.LocalDateTime.now());
                userRepository.save(user);
                encodedPassword = user.getPassword();
            }
        } else if (role.equals("VENDOR")) {
            Vendor vendor = vendorRepository.findByEmail(email);
            if (vendor != null && passwordEncoder.matches(password, vendor.getPassword())) {
                vendor.setLastLogin(java.time.LocalDateTime.now());
                vendorRepository.save(vendor);
                encodedPassword = vendor.getPassword();
            }
        } else if (role.equals("ADMIN")) {
            Admin admin = adminRepository.findByEmail(email);
            if (admin != null && passwordEncoder.matches(password, admin.getPassword())) {
                admin.setLastLogin(java.time.LocalDateTime.now());
                adminRepository.save(admin);
                encodedPassword = admin.getPassword();
            }
        }

        if (encodedPassword == null) {
            throw new RuntimeException("Invalid email or password");
        }

        // 🔹 STEP 3: Generate JWT
        String tokenString = jwtUtil.generateToken(email, role);

        // 🔹 STEP 4: Store token in DB
        Token token = new Token();
        token.setEmail(email);
        token.setToken(tokenString);

        return tokenRepository.save(token);
    }
}
