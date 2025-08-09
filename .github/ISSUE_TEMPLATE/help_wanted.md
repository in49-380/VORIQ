name: Help wanted
description: Request for assistance.
title: "[Help Wanted]: "
labels: ["help wanted"]
body:
  - type: textarea
    id: description
    attributes:
      label: Description
      description: Clearly describe the help you need.
      placeholder: Explain the problem or area where assistance is required.
    validations:
      required: true

  - type: textarea
    id: tried
    attributes:
      label: What I've Tried
      description: Describe any steps you've already taken to solve the problem.
      placeholder: |
        1. Attempted solution A...
        2. Attempted solution B...
    validations:
      required: true

  - type: textarea
    id: additional
    attributes:
      label: Additional context
      description: Add any other context or information that may help.

  - type: textarea
    id: acceptance_criteria
    attributes:
      label: Acceptance Criteria
      description: List all measurable conditions for considering this request resolved.
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
