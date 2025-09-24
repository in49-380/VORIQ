## 🚗 VORIQ Car Catalog API Tests

Automated end-to-end test to resolve `carId` with self-healing logic.

📁 Path: `voriq-car-catalog/`
profile
Qwen3-Max
10:31 am
Конечно! Вот готовый файл README.md в виде единого текста — просто скопируйте всё содержимое ниже и вставьте в файл README.md внутри папки VORIQ/qa/automation/rest-api/voriq-car-catalog/.

markdown


1
2
3
4
5
6
7
8
9
10
11
⌄
⌄
# 🚗 VORIQ Self-Healing Car Catalog API Test

> Automated end-to-end test to resolve a valid `carId` from the multi-level car catalog — with built-in **self-healing** logic that recovers from missing or invalid IDs at any step.

This collection automatically discovers a working chain:  
`Brand → Model → Year → Engine → Transmission → Wheel Drive → carId`

---

## 📁 Project Structure

voriq-car-catalog/
├── collections/
│ └── Voriq_Self-Healing_Test.postman_collection.json
├── environments/
│ └── QA.postman_environment.json
├── .env.example
├── .gitignore
└── README.md
### ▶️ How to Run

1. Prerequisites
Make sure you have:

Node.js (v18+)
Newman
Allure CLI
1. Install globally:
npm install -g newman newman-reporter-allure
2. Local Execution
# Navigate to test directory
cd qa/automation/rest-api/voriq-car-catalog

# Create .env from example
cp .env.example .env

# Edit .env with real values:
#   BASE_URL=http://voriq.info:8084/api
#   ACCESS_TOKEN=your_actual_bearer_token
nano .env  # or open in VS Code

# Run the test
newman run collections/Voriq_Self-Healing_Test.postman_collection.json \
  --env-var "baseUrl=$(grep BASE_URL .env | cut -d '=' -f2)" \
  --env-var "accessToken=$(grep ACCESS_TOKEN .env | cut -d '=' -f2)" \
  --reporters cli,allure \
  --reporter-allure-export ./newman/

# View interactive Allure report
allure serve newman/

3. What This Test Does
The test executes a 7-step dependent chain:

Fetches a valid Brand
Finds a Model for that brand
Selects a Year for the model
Picks an Engine for the year
Chooses a Transmission for the engine
Gets a Wheel Drive type
Resolves all IDs into a final carId
✅ If any step fails (404, empty array, invalid ID), the self-healing logic:

Automatically tries alternative entries
Updates environment variables
Restarts from the failed or previous step
Continues until a valid carId is found or all options are exhausted
4. Expected Outcome
✅ All requests pass (status 200)
✅ Final carId is saved in environment and printed in console
✅ Allure report shows:
Each step with logs
Time taken per request
Final carId value
Self-healing attempts (visible in console logs)

5. CI/CD Execution
This test is also triggered automatically via GitHub Actions:

On every push to main or dev
Daily at 03:00 UTC (scheduled run)
Allure report is archived as a workflow artifact
To view CI results:

Go to GitHub → Actions
Open the latest VORIQ Car Catalog API Tests workflow
Download the allure-report artifact
Unzip and run allure serve locally
