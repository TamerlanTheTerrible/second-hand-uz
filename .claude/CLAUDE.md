# CLAUDE.md — Core Instruction Set for All Features / Fixes

## 00 — Enforcement Framework
<contents of 00-enforcement-framework.md>
- Always classify the change risk: LOW, MEDIUM, HIGH, or CRITICAL
- Apply Tier 1 configs for every feature/fix automatically
- Apply Tier 2 configs based on feature metadata tags
- Apply Tier 3 configs periodically or for release-level checks
- Refuse implementations that violate mandatory rules

## 00 — Risk Classification
<contents of 00-risk-classification.md>
- LOW: minor fix, internal refactor, logging
- MEDIUM: new methods, new endpoint in existing module
- HIGH: new module, external integration, authentication changes
- CRITICAL: security logic, payment or authorization logic, public API changes
- Map risk to required config files automatically

---

# Notes for Claude

1. **Always enforce these rules for all feature/fix MD files**.
2. **Conditional configs (01–08)** are applied only when specified in the feature MD `.meta.run_configs`.
3. Respect the `.meta.tags` and `.meta.risk` to decide which optional configs to include.
4. Output implementation plans/code suggestions that **fully satisfy these instructions**.
5. Do not ignore security, testing, or quality rules under any circumstances.
6. If a rule cannot be satisfied, explain why and suggest remediation.