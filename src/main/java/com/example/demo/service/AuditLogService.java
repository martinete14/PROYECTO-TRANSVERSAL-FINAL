package com.example.demo.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.demo.config.AuthSessionKeys;
import com.example.demo.model.AuditLog;
import com.example.demo.model.Usuario;
import com.example.demo.repository.AuditLogRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class AuditLogService {

    private static final int DEFAULT_LIMIT = 80;
    private static final int MIN_LIMIT = 10;
    private static final int MAX_LIMIT = 300;

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public List<AuditLog> getRecent(String actor, String action, Integer limit) {
        String actorFilter = normalizeFilter(actor);
        String actionFilter = normalizeFilter(action);
        int safeLimit = normalizeLimit(limit);

        return auditLogRepository.searchRecent(actorFilter, actionFilter, PageRequest.of(0, safeLimit));
    }

    public void logFromSession(HttpSession session, String actionType, String details, String targetPath, String ipAddress) {
        if (session == null) {
            logAnonymous(actionType, details, targetPath, ipAddress);
            return;
        }

        AuditLog log = baseLog(actionType, details, targetPath, ipAddress);

        Object userId = session.getAttribute(AuthSessionKeys.AUTH_USER_ID);
        if (userId instanceof Long id) {
            log.setActorUserId(id);
        }

        Object name = session.getAttribute(AuthSessionKeys.AUTH_NAME);
        if (name != null) {
            log.setActorName(name.toString());
        }

        Object role = session.getAttribute(AuthSessionKeys.AUTH_ROLE);
        if (role != null) {
            log.setActorRole(role.toString());
        }

        auditLogRepository.save(log);
    }

    public void logForUser(Usuario user, String actionType, String details, String targetPath, String ipAddress) {
        AuditLog log = baseLog(actionType, details, targetPath, ipAddress);
        if (user != null) {
            log.setActorUserId(user.getId());
            log.setActorName(user.getNombre());
            log.setActorRole(user.getRol());
        }
        auditLogRepository.save(log);
    }

    public void logAnonymous(String actionType, String details, String targetPath, String ipAddress) {
        auditLogRepository.save(baseLog(actionType, details, targetPath, ipAddress));
    }

    private AuditLog baseLog(String actionType, String details, String targetPath, String ipAddress) {
        AuditLog log = new AuditLog();
        log.setActionType(truncate(defaultText(actionType, "UNSPECIFIED"), 80));
        log.setDetails(truncate(defaultText(details, "Sin detalle"), 4000));
        log.setTargetPath(truncate(targetPath, 180));
        log.setIpAddress(truncate(ipAddress, 45));
        return log;
    }

    private String normalizeFilter(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null) {
            return DEFAULT_LIMIT;
        }
        return Math.max(MIN_LIMIT, Math.min(MAX_LIMIT, limit));
    }

    private String defaultText(String value, String fallback) {
        if (!StringUtils.hasText(value)) {
            return fallback;
        }
        return value.trim();
    }

    private String truncate(String value, int max) {
        if (!StringUtils.hasText(value)) {
            return value;
        }
        String clean = value.trim();
        return clean.length() > max ? clean.substring(0, max) : clean;
    }
}
