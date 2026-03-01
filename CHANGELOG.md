# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [1.0.0] - 2026-03-01

### Added

#### Backend
- **User module**: registration, login (JWT), public profile, seller rating aggregation
- **Listing module**: CRUD, image upload (multipart), full-text search with brand/price filters
- **Order module**: order lifecycle (CREATED → PAID → SHIPPED → COMPLETED / CANCELED)
- **Payment module**: ATMOS payment gateway integration with OAuth token fetch and webhook HMAC-SHA256 verification
- **Review module**: buyer-to-seller reviews after completed orders; seller rating auto-update
- **Common infrastructure**:
  - JWT stateless authentication (jjwt 0.12.6)
  - Global exception handler with structured `ErrorResponse`
  - MDC-based `traceId` propagation (`TraceIdFilter`)
  - Audit logging service
  - In-memory rate limiting (token bucket, per IP)
  - Input sanitization (`ContentSanitizer`) for XSS prevention
  - OpenAPI 3 / Swagger UI
  - Security headers (CSP, X-Frame-Options, X-Content-Type-Options)
- **Database**: Liquibase migrations for `users`, `listings`, `orders`, `reviews`
- **Testing**: Unit tests for all service classes; integration test with Testcontainers (PostgreSQL)
- **Static analysis**: SpotBugs, PMD, Checkstyle Maven plugins
- **Docker**: Multi-stage Dockerfile (eclipse-temurin:21), docker-compose with health check

#### Frontend
- React 18 + Vite SPA
- Pages: Home (browse/search), Login, Register, Listing Detail, Create Listing, Orders, Seller Profile
- Components: Navbar, ListingCard, SearchBar, ProtectedRoute
- API client (axios) with Bearer token injection and 401 auto-redirect
- React Query for server-state caching
- Auth context with localStorage persistence

### Security
- No hardcoded secrets — all sensitive values via environment variables
- Passwords hashed with BCrypt
- Stateless JWT sessions
- CORS configured via `CORS_ALLOWED_ORIGINS` env var
- Webhook authenticity verified via HmacSHA256 signature
- Non-root Docker user (`appuser`)
