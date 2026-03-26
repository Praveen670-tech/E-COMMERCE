package com.example.E_COMMERCE.Backend.Repository;

import com.example.E_COMMERCE.Backend.Entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VendorRepository extends JpaRepository<Vendor, Long> {

    Vendor findByEmail(String email);

    @Query("SELECT v.password FROM Vendor v WHERE v.email = :email")
    String findPasswordByEmail(String email);
}
