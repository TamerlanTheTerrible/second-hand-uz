package me.timur.secondhanduz.common.logging;

/**
 * Port for structured audit logging — enables clean mocking in unit tests.
 */
public interface AuditLogger {

    void log(String action, Long actorId, Long resourceId, String details);

    void log(String action, Long actorId, String details);
}
