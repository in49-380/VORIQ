# 📘 Commit and Branch Naming Rules for the VORIQ Project

This document defines the required standards for **branch names** and **commit messages** to enable automated linking with GitHub Issues and Projects Kanban boards.

---

## 🟦 1. Branch Naming Convention

Format:

```
<type>/<issue-id>-<short-description>
```

### Examples:

* `feature/001-login-form`
* `bugfix/017-fix-parser-timeout`
* `hotfix/099-db-connection`
* `refactor/034-cleanup-auth-service`
* `test/020-user-flow-tests`
* `docs/010-update-readme`

### Accepted Types:

* `feature` — New functionality
* `bugfix` — Bug fixes
* `hotfix` — Urgent fixes
* `refactor` — Code cleanup or restructuring
* `test` — Tests creation or updates
* `docs` — Documentation updates
* `chore` — Maintenance or tooling tasks

---

## 🟩 2. Commit Message Format

First line format:

```
<type>: #<issue-id> <short summary>
```

### Examples:

```
feature: #001 Add login form
bugfix: #017 Fix parser timeout
docs: #010 Update README with setup instructions
```

* Use imperative mood ("Add", "Fix", not "Added", "Fixed").
* Limit the first line to 72 characters.
* Add a detailed description below, separated by a blank line, if needed.

---

## 🟨 3. GitHub Issues and Projects Automation

To link commits and pull requests with issues automatically:

* Include the following keywords in the message body or PR description:

  * `Closes #001`
  * `Fixes #017`
  * `Resolves #020`

This will automatically close the issue once the PR is merged.

---

## 🟥 4. General Guidelines

* One commit = one logical change.
* Branch names must be in **lowercase**, with hyphens as word separators.
* Do not commit directly to main. Use dev as the integration branch.
* All pull requests must be merged into the **dev** branch.
* Use Draft PRs if the feature is still in development.

---

## 📎 Full Example

**Branch:**
`feature/042-add-car-review-page`

**Commit:**

```
feature: #042 Add car review page

Created initial template, connected to router, added placeholders for review content.
Closes #042
```
