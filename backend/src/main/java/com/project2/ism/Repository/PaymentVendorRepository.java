package com.project2.ism.Repository;

import com.project2.ism.Model.Payment.PaymentVendor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentVendorRepository extends JpaRepository<PaymentVendor, Long> {

    boolean existsByVendorName(String vendorName);
}
