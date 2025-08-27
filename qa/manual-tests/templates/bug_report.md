## name
Bug Report
## description
Standard template for reporting defects
## title
[BUG] Short problem description
## labels
```yaml
- bug
- QA

```
## assignees
```yaml
[]

```
## body
```yaml
- attributes:
    label: Affected Module/Page
    placeholder: e.g. /login, Auth API
  id: area
  type: input
- attributes:
    default: 2
    label: Severity
    options:
    - Blocker
    - Critical
    - Major
    - Minor
    - Trivial
  id: severity
  type: dropdown
  validations:
    required: true
- attributes:
    default: 1
    label: Priority
    options:
    - High
    - Medium
    - Low
  id: priority
  type: dropdown
  validations:
    required: true
- attributes:
    label: Environment
    placeholder: 'OS: macOS Monterey 12

      Browser: Chrome 138

      Build: v1.2.0

      Device: Desktop

      '
  id: env
  type: textarea
  validations:
    required: true
- attributes:
    label: Steps to Reproduce
    placeholder: '1) Open /login

      2) Enter user@example.com / wrong123

      3) Click "Sign in"

      '
  id: steps
  type: textarea
  validations:
    required: true
- attributes:
    label: Expected Result
    placeholder: Localized error message is displayed, login is not successful
  id: expected
  type: textarea
  validations:
    required: true
- attributes:
    label: Actual Result
    placeholder: Message is in English only, localization missing
  id: actual
  type: textarea
  validations:
    required: true
- attributes:
    label: Reproducibility
    options:
    - label: Always
    - label: Sometimes
    - label: Once
  id: reproducibility
  type: checkboxes
- attributes:
    description: Screenshots, videos, HAR/log files, dumps, or links to PR/commits
    label: Attachments
    placeholder: Paste links or attach files
  id: attachments
  type: textarea
- attributes:
    description: Link to related test cases, checklists, or PRs
    label: Related Issues
    placeholder: 'Fixes #123; Related to #58'
  id: links
  type: textarea

```