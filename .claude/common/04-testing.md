# Testing Configuration

## Unit Testing
- Test each public method
- Include:
    - Valid inputs
    - Invalid inputs
    - Edge cases
- Use JUnit 5
- Mock external dependencies
- Test private logic via public APIs

## Coverage
- Minimum 80% coverage required

## Integration Testing
- Required for service boundaries
- Required for DB interactions
- Use Testcontainers for DB tests

## E2E Testing
- Required for critical user flows

## Naming Convention
should_<expectedBehavior>_when_<condition>

## Performance
- Tests must execute quickly
- Avoid unnecessary heavy integration tests

## Other
- Do not change / refactor existing tests without permission