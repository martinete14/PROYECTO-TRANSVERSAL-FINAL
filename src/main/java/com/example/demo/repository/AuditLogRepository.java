package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("""
        SELECT a
        FROM AuditLog a
        WHERE (:actor IS NULL OR lower(a.actorName) LIKE lower(concat('%', :actor, '%')))
          AND (:action IS NULL OR lower(a.actionType) LIKE lower(concat('%', :action, '%')))
        ORDER BY a.occurredAt DESC
        """)
    List<AuditLog> searchRecent(
        @Param("actor") String actor,
        @Param("action") String action,
        Pageable pageable
    );
}
