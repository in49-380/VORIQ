name: Feature request
description: Suggest a new feature or improvement.
title: "[Feature]: "
labels: ["feature"]
body:
  - type: textarea
    id: description
    attributes:
      label: Description
      description: Describe the feature or improvement you'd like to see.
      placeholder: A clear and concise description of the idea.
    validations:
      required: true

  - type: textarea
    id: motivation
    attributes:
      label: Motivation
      description: Why is this feature important?
      placeholder: Explain the problem this will solve or the value it brings.
    validations:
      required: true

  - type: textarea
    id: additional
    attributes:
      label: Additional context
      description: Add any other context or screenshots about the feature request here.

  - type: textarea
    id: acceptance_criteria
    attributes:
      label: Acceptance Criteria
      description: List all measurable conditions to accept this feature as complete.
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
