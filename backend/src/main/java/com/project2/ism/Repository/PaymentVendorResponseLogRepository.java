package com.project2.ism.Repository;


import com.project2.ism.Model.Payment.PaymentVendorResponseLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentVendorResponseLogRepository extends JpaRepository<PaymentVendorResponseLog, Long> {
    // Add custom queries if needed
}
