
## name
QA Checklist
## description
Template for creating a checklist of multiple test checks
## title
[QA] Short checklist title
## labels
```yaml
- QA
- checklist

```
## assignees
```yaml
[]

```
## body
```yaml
- attributes:
    value: One issue = one checklist. Use markdown checkboxes to track progress.
  type: markdown
- attributes:
    label: Scope / Release / Feature
    placeholder: e.g. Onboarding v1.2
  id: scope
  type: input
- attributes:
    label: Preconditions
    placeholder: '- Build v1.2.0 deployed

      - Test user created

      '
  id: preconditions
  type: textarea
- attributes:
    description: Write each check as a markdown checkbox. Example below.
    label: Checklist Items
    placeholder: '- [ ] UI: Login button is visible

      - [ ] Valid credentials → successful login

      - [ ] Invalid password → correct error message

      - [ ] Language switch updates UI texts immediately

      - [ ] Language choice persists after reload

      - [ ] Consent modal appears only on first visit

      - [ ] External links (/terms, /privacy, /cookies) open in a new tab

      '
  id: checklist
  type: textarea
- attributes:
    label: Notes and Issues
    placeholder: Briefly record deviations. Create separate bug reports and link them
      here.
  id: notes
  type: textarea

```