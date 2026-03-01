# Risk Classification Model

## LOW Risk
Examples:
- Small bug fix
- Refactor inside same class
- Logging change
- Minor validation change

Required Configs:
- 03-security.md
- 04-testing.md
- 06-code-quality.md

---

## MEDIUM Risk
Examples:
- New method in service
- New endpoint inside existing module
- Database query modification
- New validation logic

Required Configs:
- Tier 1 configs
- 01-java.md
- 08-operability-and-resilience.md (if external call exists)

---

## HIGH Risk
Examples:
- New module
- External service integration
- Authentication changes
- Database schema change
- Cross-module interaction

Required Configs:
- Tier 1 configs
- 01-java.md
- 02-architecture.md
- 08-operability-and-resilience.md
- 07-documentation.md

---

## CRITICAL Risk
Examples:
- Security logic modification
- Payment processing logic
- Authorization logic
- Public API contract changes
- Production hotfix

Required Configs:
- ALL configs
- Manual review required
- Explicit backward compatibility validation