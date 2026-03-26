package com.example.E_COMMERCE.Backend.Repository;

import com.example.E_COMMERCE.Backend.Entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Admin findByEmail(String email);

    @Query("SELECT a.password FROM Admin a WHERE a.email = :email")
    String findPasswordByEmail(String email);
}
