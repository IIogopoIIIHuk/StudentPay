package com.repo;

import com.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentDateBetween(LocalDate startOfMonth, LocalDate endOfMonth);

    List<Payment> findAllByPaymentDateBetween(LocalDate startDate, LocalDate endDate);
}