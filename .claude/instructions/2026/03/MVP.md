.meta:
risk: HIGH
tags:
- external-call
- new-endpoint
- new-module
- database-change
- authentication


Claude Code Implementation Instructions — Second-Hand Marketplace MVP

⸻

🖥 BACKEND — Implementation Checklist

Project Setup
✓	Create modular monolith structure (modules: user, listing, order, payment, review, admin UI)
✓	Configure PostgreSQL database connection
✓	Add Liquibase for database migrations
✓	Configure JWT-based authentication
✓	Implement global exception handling
✓	Implement request logging with traceId
✓	Configure environment-based configuration (dev/test/prod)
✓	Dockerize the backend application

User Module
✓	Create User entity (id, email, passwordHash, role, rating, createdAt)
✓	Implement registration endpoint
✓	Implement login endpoint (JWT issuance)
✓	Implement password hashing using BCrypt
✓	Implement get user profile endpoint
✓	Implement seller rating calculation logic
✓	Add validation for user input (email format, password strength)

Listings Module
✓	Create Listing entity (id, sellerId, title, description, price, size, brand, condition, status, createdAt)
✓	Implement image upload (local storage for MVP or S3-compatible storage)
✓	Implement create listing endpoint
✓	Implement edit listing endpoint (validate ownership)
✓	Implement delete listing endpoint (validate ownership)
✓	Implement get single listing endpoint
✓	Implement get listings endpoint with pagination
✓	Implement basic search (title + brand using LIKE query)
✓	Prevent editing of sold listings

Order Module
✓	Create Order entity (id, buyerId, listingId, status, totalPrice, createdAt)
✓	Add database constraint to prevent double purchase of listing
✓	Implement create order endpoint
✓	Implement order status flow (CREATED → PAID → SHIPPED → COMPLETED → CANCELED)
✓	Implement cancel order endpoint (only if not paid)
✓	Validate listing availability before order creation

Payment Integration
✓	Integrate ATMOS payment session (https://docs.atmos.uz/ru/#3394112aa9)
✓	Implement webhook endpoint for payment confirmation
✓	Verify webhook signature
✓	Update order status after successful payment
✓	Implement basic escrow logic (manual release after completion)

Reviews Module
✓	Create Review entity (id, reviewerId, reviewedUserId, rating, comment, createdAt)
✓	Implement create review endpoint
✓	Allow reviews only for completed orders
✓	Implement endpoint to fetch reviews for a seller

Security & Safety
✓	Validate ownership before modifying resources
✓	Implement input validation (Bean Validation)
✓	Implement basic rate limiting
✓	Sanitize user-generated content
✓	Add CORS configuration
✓	Add basic audit logging for critical actions

⸻

🌐 FRONTEND — Implementation Checklist

Project Setup
✓	Create React project
✓	Configure API client (fetch or Axios wrapper)
•	Configure authentication using httpOnly cookies
✓	Implement global layout and routing structure
✓	Configure environment variables

Authentication
✓	Create registration page
✓	Create login page
✓	Implement logout functionality
✓	Implement protected routes
✓	Display authentication errors clearly

Marketplace Core
✓	Implement homepage with listings grid
✓	Implement listing details page
✓	Implement search bar
✓	Implement filters (price range, size, brand)
•	Implement pagination or infinite scroll
✓	Handle loading and empty states

Selling Flow
✓	Implement create listing form
✓	Implement image upload component
•	Implement edit listing page
✓	Implement delete listing functionality
✓	Implement seller profile page with listings

Buying Flow
✓	Implement "Buy" button
✓	Redirect to Stripe checkout
•	Implement order success page
✓	Implement order history page
✓	Display order status clearly

Reviews UI
✓	Implement leave review form
✓	Display seller rating on profile
✓	Display reviews list on seller profile

⸻

🚀 Future Architecture Preparation (Do Not Fully Implement in MVP)
✓	Design system to support modular extraction into microservices
✓	Abstract payment provider behind interface
✓	Prepare search abstraction for future Elasticsearch integration
•	Prepare event-driven design (domain events for order/payment)
•	Plan Redis integration for caching and rate limiting
•	Plan object storage abstraction layer