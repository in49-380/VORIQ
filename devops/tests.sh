#!/bin/bash
set -euo pipefail

echo "== Clone repo =="
git clone --depth=1 --branch "${REPO_BRANCH}" "${REPO_URL}" /src

mkdir -p "${RESULTS_DIR}" "${REPORT_DIR}" "${HISTORY_CACHE}"

echo "== Run UI tests =="
pushd /src/qa/automation/ui >/dev/null
mvn -B -e clean test \
-Dbase.url="${BASE_URL}" \
-Dselenium.remote.url="${SELENIUM_REMOTE_URL}" \
-Dallure.results.directory="${RESULTS_DIR}"
# локальный отчёт модуля (не обязателен, но полезен для history)
mvn -B io.qameta.allure:allure-maven:report \
-Dallure.results.directory="${RESULTS_DIR}" \
-Dallure.report.directory="${REPORT_DIR}"
popd >/dev/null

echo "== Run API tests =="
pushd /src/qa/automation/rest-api >/dev/null
mvn -B -e clean test \
-Dbase.url="${BASE_URL}" \
-Dallure.results.directory="${RESULTS_DIR}"
# локальный отчёт модуля (не обязателен, но полезен для history)
mvn -B io.qameta.allure:allure-maven:report \
-Dallure.results.directory="${RESULTS_DIR}" \
-Dallure.report.directory="${REPORT_DIR}"
popd >/dev/null

echo "== Preload history to results =="
rsync -a "${REPORT_DIR}/history/" "${RESULTS_DIR}/history/" || true

echo "== Build aggregated report (one for both) =="
# соберём единый статический отчёт из общей папки результатов
mvn -f /src/qa/automation/ui/pom.xml -B io.qameta.allure:allure-maven:report \
-Dallure.results.directory="${RESULTS_DIR}" \
-Dallure.report.directory="${REPORT_DIR}"

echo "== Save history back to cache =="
if [ -d "${REPORT_DIR}/history" ]; then
rsync -a "${REPORT_DIR}/history/" "${HISTORY_CACHE}/history/" || true
fi

echo "All done. Aggregated report at ${REPORT_DIR}/index.html"
