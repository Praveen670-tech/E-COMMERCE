package com.example.E_COMMERCE.Backend.Security;

import com.example.E_COMMERCE.Backend.Entity.UserRole;
import com.example.E_COMMERCE.Backend.Repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        // 🔍 Fetch role mapping using email
        UserRole userRole = userRoleRepository.findByEmail(email);

        if (userRole == null) {
            throw new UsernameNotFoundException(
                    "No user found with email: " + email
            );
        }

        // ✅ Return Spring Security compatible UserDetails
        return new CustomUserDetails(
                userRole.getEmail(),
                userRole.getRoleName()   // ADMIN / USER / VENDOR
        );
    }
}
