# DevOps & CI/CD Configuration

- Use GitHub Actions or GitLab CI
- Run tests on every Pull / Merge Request
- Run lint checks on every Pull Request
- Fail build on lint errors
- Build artifacts on merge to main branch
- Run security scan in CI
- Auto-deploy to staging
- Manual approval required for production deployment

# Dependency Management Configuration

- Use dependency locking
- Avoid latest versions in production
- periodically_update_dependencies: false
- Scan for vulnerabilities
- Remove unused dependencies regularly