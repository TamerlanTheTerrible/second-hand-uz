# Observability

- Use structured logging
- Persist a new log file for each day
- Use correlation IDs
- Track metrics for critical operations
- Provide health check endpoints
- Use distributed tracing (if microservices)

# Performance

- Avoid N+1 queries
- Use caching where appropriate
- Profile before optimizing
- Load test critical paths

# Resilience

- Set timeouts for all external calls
- Use circuit breakers
- Retry with backoff
- Make operations idempotent where applicable

# Backward Compatibility

- Version APIs
- Do not break public contracts without version bump
- Maintain database migration scripts (Flyway/Liquibase)