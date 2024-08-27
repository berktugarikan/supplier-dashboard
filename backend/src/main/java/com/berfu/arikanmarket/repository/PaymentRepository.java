package com.berfu.arikanmarket.repository;

import com.berfu.arikanmarket.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
