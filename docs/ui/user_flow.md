### User Flow: Vehicle Reliability Analysis Platform

## Overview

The application is a single-page interface (SPA) operating entirely through a root `index.html` file. Navigation is handled via dynamic UI state changes rather than URL paths. The file itself is served in a way that **keeps the address bar clean**, i.e., the browser only shows the domain (`https://voriq.info`) without revealing `index.html` or any internal paths.


#### Step-by-Step Flow

| Step | State/Screen          | User Action                        | UI Components                                                                                                      | System Response / Transition                                                          |
| ---- | --------------------- | ---------------------------------- | ------------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------- |
| 0    | Cookie Consent Dialog | Accept cookies or decline          | `Dialog`, `Checkbox`, `Button (Accept / Reject)`                                                                   | Proceed only if accepted                                                              |
|      |                       | View legal documents               | `Hyperlink`: Terms of Use, Privacy Policy, Cookie Policy                                                           | Opens external link in new tab                                                        |
| 1    | Login Screen          | Sign in via Google                 | `Button (Google OAuth)`                                                                                            | Save token, transition to selection screen                                            |
|      |                       | Open GitHub repository             | `IconButton (GitHub)`                                                                                              | Opens GitHub in new tab                                                               |
| 2    | Select Vehicle Brand  | Choose car brand                   | `ComboBox`, `Button (Next)`                                                                                        | Load models                                                                           |
| 3    | Select Vehicle Model  | Choose car model                   | `ComboBox`, `Button (Next)`                                                                                        | Load years                                                                            |
| 4    | Select Year/Period    | Choose manufacturing year or range | `ComboBox`, `DateRangePicker`, `Button (Next)`                                                                     | Load engine options                                                                   |
| 5    | Select Engine Type    | Choose engine type                 | `ComboBox`, `Button (Start Analysis)`                                                                              | Enable analysis button                                                                |
| 6    | Analysis Running      | Start the analysis                 | `Button`, `ProgressIndicator`, `Snackbar`, `Disclaimer Dialog (if first run)`, `Modal Loader with Timer/Animation` | Show disclaimer (only once per session), then show loader while data aggregation runs |
|      |                       | Encounter error                    | `Error Dialog`, `Retry Button`                                                                                     | Show error with retry/cancel options                                                  |
| 7    | Analysis Results      | View statistics and feedback       | `Charts`, `ListView`, `Tabs`, `Button (New search)`, `ExpandableList (Parsed URLs)`                                | Display results and list of visited resources                                         |
| 8    | Post-Analysis Options | Repeat process or logout           | `Button (Select another car)`, `Button (Logout)`                                                                   | Clear selection or token                                                              |


#### Notes:

* All application logic runs within a single index file.
* The file is served via Envoy or other proxy so that `index.html` never appears in the browser’s address bar.
* No internal routing; navigation is UI state-driven.
* External legal links (Terms, Privacy, Cookies) are accessible before login.
* GitHub icon available at login and footer post-login.
* Legal links can also be retrieved via API (e.g., `/api/meta/privacy`, `/api/meta/terms`, `/api/meta/cookies`) for dynamic injection.
* The list of visited URLs used by the parser is included in the results. Displaying this list is legally permitted as long as only public, non-personal sources are accessed and no copyrighted content is copied or stored.
* A disclaimer is shown the first time a user initiates parsing in a session. It explicitly states that the analysis is based on publicly available user opinions, which may contain subjective or incorrect information. The user must acknowledge this disclaimer to proceed.
* Logout option is available in the application footer.
* Multilanguage support is provided for all interface content; language selection is available in the UI.
* Contextual onboarding hints are displayed for first-time users.
* A visual modal loader with optional countdown or animation is shown during analysis to enhance feedback during long operations.

---

#### External Links (Static or via API):

* Terms of Use:  `/api/meta/terms`
* Privacy Policy:  `/api/meta/privacy`
* Cookie Policy:  `/api/meta/cookies`
* GitHub Repository: [https://github.com/in49-380/VORIQ](https://github.com/in49-380/VORIQ`)