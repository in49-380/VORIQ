name: Good first issue
description: A simple task suitable for newcomers.
title: "[Good First Issue]: "
labels: ["good first issue"]
body:
  - type: textarea
    id: task_description
    attributes:
      label: Task Description
      description: Clearly describe the task to be done.
      placeholder: A clear and beginner-friendly description.
    validations:
      required: true

  - type: textarea
    id: steps
    attributes:
      label: Steps to Get Started
      description: Instructions for starting this task.
      placeholder: |
        For **project members**:
        1. Create a new branch from `dev` in this repository.
        2. Implement the required changes.
        3. Commit and push your branch.
        4. Open a Pull Request linking this issue.

        For **external collaborators** (non-members):
        1. Fork the repository.
        2. Create a branch from `dev` in your fork.
        3. Implement the required changes.
        4. Commit and push to your fork.
        5. Open a Pull Request from your fork to this repository, linking this issue.
    validations:
      required: true

  - type: textarea
    id: additional
    attributes:
      label: Additional context
      description: Add any helpful links or information for beginners here.

  - type: textarea
    id: acceptance_criteria
    attributes:
      label: Acceptance Criteria
      description: List all measurable conditions to accept this task as complete.
      placeholder: |
        - [ ] AC1
        - [ ] AC2
    validations:
      required: true

  - type: input
    id: dependencies
    attributes:
      label: Dependencies (blocks / blocked by)
      description: Related issues (e.g. #123) or leave empty.
      placeholder: "#123"
