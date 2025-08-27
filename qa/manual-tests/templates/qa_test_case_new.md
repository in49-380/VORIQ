
## name
QA Test Case
## description
Template for describing a single test case
## title
[QA] Short test case title
## labels
```yaml
- QA
- test-case

```
## assignees
```yaml
[]

```
## body
```yaml
- attributes:
    value: Please fill in the fields — one issue = one test case.
  type: markdown
- attributes:
    label: Component/Module
    placeholder: e.g. Auth → Login
  id: scope
  type: input
- attributes:
    description: What exactly are we testing and why?
    label: Goal
    placeholder: Verify handling of invalid password during login
  id: goal
  type: textarea
  validations:
    required: true
- attributes:
    label: Preconditions
    placeholder: '- User account exists

      - Access to staging environment

      '
  id: preconditions
  type: textarea
- attributes:
    label: Test Data
    placeholder: 'login: user@example.com

      password: wrong123

      '
  id: data
  type: textarea
- attributes:
    label: Steps
    placeholder: '1) Open /login

      2) Enter login / wrong password

      3) Click "Sign in"

      '
  id: steps
  type: textarea
  validations:
    required: true
- attributes:
    label: Expected Result
    placeholder: Error message is displayed, login is not successful
  id: expected
  type: textarea
  validations:
    required: true
- attributes:
    description: To be filled after execution
    label: Actual Result
    placeholder: —
  id: actual
  type: textarea
- attributes:
    default: 1
    label: Priority
    options:
    - High
    - Medium
    - Low
  id: priority
  type: dropdown
- attributes:
    label: Environment
    options:
    - label: Web (Chrome)
    - label: Web (Firefox)
    - label: Mobile (Android)
    - label: Mobile (iOS)
  id: env
  type: checkboxes
- attributes:
    description: Screenshots, logs, videos, or links to automated tests
    label: Artifacts
    placeholder: Paste links or attach files
  id: artifacts
  type: textarea

```