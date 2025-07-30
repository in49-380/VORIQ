# 🗂 Issue and Ticket Usage Policy

## 📌 General Guidelines

* Every task in the project **must begin with a GitHub Issue**.
* All discussions and clarifications should happen **within the corresponding Issue**.

---

## 🚧 Taking a Task

* Assign yourself to the Issue before starting work.
* Then, **create a new branch from `dev`**, following the rules in [Commit and Branch Rules](./Commit_and_Branch_Rules.md).
* The branch name **must include the Issue number**.

---

## 🔃 Pull Requests

* Create a Pull Request **only from a branch linked to an Issue**, and always target the `dev` branch.
* The PR description must reference the Issue (e.g., `Closes #001`).
* One PR = one Issue/task.

---

## 🧼 Clean Process

* Never create a PR without a related Issue.
* Do not take an Issue that is already assigned to someone else.
* The `main` branch must not be used for development.

---

## 🔄 Updates and Discussion

* If a task needs clarification, **comment inside the Issue thread**.
* Do not close Issues manually — they must be closed via `Closes #...` in the PR body.

---

Following this workflow ensures traceability, clarity, and automation of progress tracking in GitHub Projects.
