
package com.trustvault.trustvault.repository;

import com.trustvault.trustvault.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUsernameOrderByTimestampDesc(
            String username);
}

