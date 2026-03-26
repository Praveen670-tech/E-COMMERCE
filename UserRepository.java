package com.example.E_COMMERCE.Backend.Repository;

import com.example.E_COMMERCE.Backend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    @Query("SELECT u.password FROM User u WHERE u.email = :email")
    String findPasswordByEmail(String email);
}
