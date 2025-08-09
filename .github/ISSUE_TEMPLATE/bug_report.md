name: Bug report
description: Report a problem or unexpected behavior.
title: "[Bug]: "
labels: ["bug"]
body:
  - type: textarea
    id: description
    attributes:
      label: Description
      description: Clearly describe the bug.
      placeholder: A clear and concise description of what the bug is.
    validations:
      required: true

  - type: textarea
    id: steps
    attributes:
      label: Steps to Reproduce
      description: How can we reproduce the issue step-by-step?
      placeholder: |
        1. Go to '...'
        2. Click on '...'
        3. See error
    validations:
      required: true

  - type: textarea
    id: expected
    attributes:
      label: Expected behavior
      description: What did you expect to happen?
    validations:
      required: true

  - type: textarea
    id: actual
    attributes:
      label: Actual behavior
      description: What actually happened?
    validations:
      required: true

  - type: input
    id: environment
    attributes:
      label: Environment
      description: OS, Browser/Version, other relevant info.
      placeholder: "OS: Ubuntu 24.04; Browser: Firefox 118"

  - type: textarea
    id: additional
    attributes:
      label: Additional context
      description: Add any other context about the problem here.

  - type: textarea
    id: acceptance_criteria
    attributes:
      label: Acceptance Criteria
      description: List all measurable conditions to accept this bug fix.
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
