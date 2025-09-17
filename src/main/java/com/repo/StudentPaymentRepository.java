package com.repo;

import com.entity.StudentPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentPaymentRepository extends JpaRepository<StudentPayment, Long> {
}