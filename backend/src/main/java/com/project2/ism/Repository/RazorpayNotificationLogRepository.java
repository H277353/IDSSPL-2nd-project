package com.project2.ism.Repository;




import com.project2.ism.Model.Logs.RazorpayNotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RazorpayNotificationLogRepository extends JpaRepository<RazorpayNotificationLog, Long> {
}

