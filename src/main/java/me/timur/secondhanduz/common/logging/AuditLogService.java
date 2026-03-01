package me.timur.secondhanduz.common.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for recording structured audit log entries for critical actions.
 */
@Service
public class AuditLogService implements AuditLogger {

    private static final Logger auditLog = LoggerFactory.getLogger("AUDIT");

    /**
     * Log a critical action with actor, resource, and details.
     *
     * @param action     the action performed (e.g. USER_REGISTERED, ORDER_CREATED)
     * @param actorId    ID of the user performing the action
     * @param resourceId ID of the resource being acted upon
     * @param details    additional details
     */
    public void log(String action, Long actorId, Long resourceId, String details) {
        auditLog.info("action={} actorId={} resourceId={} details={}",
                action, actorId, resourceId, details);
    }

    /**
     * Log a critical action without a resource ID.
     */
    public void log(String action, Long actorId, String details) {
        auditLog.info("action={} actorId={} details={}", action, actorId, details);
    }
}
