# Claude Enforcement Framework

## Purpose
Define how Claude applies configuration rules to feature and fix files.

---

# Step 1 – Change Classification

Before implementing any feature or fix:
1. Classify change risk (LOW / MEDIUM / HIGH / CRITICAL)
2. Identify feature tags (put your own if missing)
3. Determine required config files

---

# Step 2 – Config Application Strategy

## Tier 1 – Always Apply
- 03-security.md
- 04-testing.md
- 06-code-quality.md

These guardrails must always be enforced.

---

## Tier 2 – Context-Based

Apply based on feature tags or risk level:

- 01-java.md → When new classes or APIs are added
- 02-architecture.md → When boundaries or modules change
- 08-operability-and-resilience.md → When external calls, async logic, or performance-sensitive logic is added

---

## Tier 3 – Governance / Release-Level

Apply:
- 05-devops-and-dependencies.md → Weekly or before release
- 07-documentation.md → Before release or when public contracts change

---

# Step 3 – Enforcement Rules

Claude must:
- Refuse insecure implementations
- Refuse untested public logic
- Refuse architecture violations in HIGH/CRITICAL changes
- Always suggest improvements if configs are partially satisfied
- Inform about the refusal

---

# Step 4 – Output Behavior

For each feature/fix:
- Identify applied config files
- Justify architectural decisions
- Explicitly mention security and test coverage impact

---


# Step 5 – Log changes
- Format the instruction file as a checklist (if not already done)
- Mark each checklist item as completed (✓) or not applicable (N/A) as you process the change
- Create a logs directory under root/.claude (if not already present)
- Create a log file every day under the.claude / logs directory (if not already present)
- Log file name: yyyy-MM-dd.log
- Log format: [timestamp] [change type] [feature tags] [config files] [others tags as per your wish] [justification] 
- Note: You can use any format you like, but make sure to include the necessary information for traceability and analysis.
- Logs must be structured and easily parsable for future analysis and audits.
- Logs must be appended in real-time as changes are processed.
- Group logs by year, month