Here’s the English version of the README.

---

# VORIQ · API Testing Framework

A set of automated tests for the **VORIQ** REST API (Java + JUnit 5 + RestAssured + Allure).
Supports a unified Allure report with history.

## Tech stack

* **Java 17+**, **Maven 3.9+**
* **JUnit 5**, **RestAssured**
* **Allure** (`allure-maven` plugin), HTTP logging
* Configs in `src/main/resources/*.properties`

## Structure

```
qa/
└── automation/
    ├── rest-api/                 # API test code (pom.xml)
    ├── ui/                       # (if used) UI tests
    ├── allure-results/           # raw results (created on run)
    ├── allure-report/            # final Allure report (generated)
    └── README.md
```

## Environment variables (secrets)

> ⚠️ Never commit real values to the repository.

Minimum set (names follow the current `ConfigManager`; adjust if your code uses different ones):

* `VORIQ_TOKEN_SECURITY` — Security service token
* `VORIQ_TOKEN` (or `VORIQ_TOKEN_CATALOG`) — catalog/main API token
* If required by tests: `USER_ID`, `USER_KEY`

Examples:

**macOS/Linux (bash/zsh):**

```bash
export VORIQ_TOKEN_SECURITY="***"
export VORIQ_TOKEN="***"
export USER_ID="***"
export USER_KEY="***"
```

**Windows PowerShell:**

```powershell
$env:VORIQ_TOKEN_SECURITY="***"
$env:VORIQ_TOKEN="***"
$env:USER_ID="***"
$env:USER_KEY="***"
```

> Notes from the original snippet like:
>
> ```
> #token=8d74f2d1-...
> #tokenSecurityService=d3cc8a...
> #userId=...  #key=...
> ```
>
> Use **your own** values and the variable names expected by `ConfigManager`.

## Quick start (run everything and build Allure)

Build, run **all API tests**, generate a unified report, and preserve history:

```bash
mkdir -p .allure/history
(cd rest-api && mvn -B clean compile test \
  generate-test-resources io.qameta.allure:allure-maven:report process-test-resources)
mvn -f ui/pom.xml -B io.qameta.allure:allure-maven:report \
  -Dallure.results.directory="$(pwd)/allure-results" \
  -Dallure.report.directory="$(pwd)/allure-report"
rsync -a ./allure-report/history/ ./.allure/history/
```

After this, the final report is available at:

```
qa/automation/allure-report/index.html
```

Open the file in your browser.

## Run API tests only

```bash
cd rest-api
mvn -B clean test
```

With a local Allure report for the module:

```bash
mvn -B io.qameta.allure:allure-maven:report
# report at target/site/allure-maven-plugin/index.html
```

## Configuration

* Base URLs and routes — in `src/main/resources/*.properties`, for example:

  ```
  baseURI.catalog=http://voriq.info:8084/api
  baseURI.security=http://voriq.info:8081/api
  ```
* Placeholder variables like `{brandId}`, `{modelId}` are resolved at the step/endpoint builder level.
* Tokens and sensitive data are read **only** from environment variables (see above).

## Debugging & logging

* RestAssured logs **URI/HEADERS/BODY** for each request.
* You may see warnings from third-party libs (e.g., Guice/Unsafe); they do not affect test execution.

## Useful commands

* Skip tests and just build:

  ```bash
  mvn -B -DskipTests package
  ```
* Run a single test class:

  ```bash
  mvn -B -Dtest=CatalogApiTest test
  ```
* Run a single test method:

  ```bash
  mvn -B -Dtest=CatalogApiTest#shouldReturnBrands test
  ```

## Standards & artifacts

* Attach run artifacts (logs, screenshots, HAR, etc.) to Allure.
* Bug reports and test cases should follow the templates in the repo (Issues → Templates).

## CI (optional)

* Cache Maven dependencies and publish `allure-report` as a build artifact.
* To keep report history, persist `.allure/history` across jobs/runs.

---

### FAQ

**Q:** The report is empty or not generated.
**A:** Ensure tests actually ran and `allure-results` is not empty; then regenerate the report with the plugin command.

**Q:** Fails with `Security service token is not set in ENV: VORIQ_TOKEN_SECURITY`.
**A:** Set the `VORIQ_TOKEN_SECURITY` environment variable (see “Environment variables”).
