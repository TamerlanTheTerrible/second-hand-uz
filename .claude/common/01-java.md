# Java Configuration

## Language & Style
- Use Java 25
- Follow Google Java Style Guide
- Naming conventions:
    - Classes → PascalCase
    - Methods → camelCase
    - Constants → UPPER_CASE

## Code Practices
- Use `record` for immutable DTOs
- Prefer immutability wherever possible
- Avoid returning null
- Prefer Optional over null
- Use var only when type is obvious
- Encapsulate all fields
- Use Builder pattern for complex objects
- Prefer Streams over loops when readable
- Avoid static mutable state
- Document public APIs with Javadoc