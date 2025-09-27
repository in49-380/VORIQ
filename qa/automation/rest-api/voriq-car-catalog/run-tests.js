// run-tests.js — версия, которая всегда генерирует отчёт
require('dotenv').config();
const { execSync } = require('child_process');
const path = require('path');
const fs = require('fs');

if (!process.env.BASE_URL || !process.env.ACCESS_TOKEN) {
  console.error('❌ BASE_URL или ACCESS_TOKEN не заданы в .env');
  process.exit(1);
}

const collection = 'collections/Voriq_Self-Healing_Test.postman_collection.json';
const envFile = 'environments/QA.postman_environment.json';
const resultsDir = path.resolve('allure-results');
const reportDir = path.resolve('allure-report');

console.log('🚀 Запуск тестов...');
let testsPassed = false;

try {
  execSync(
    `npx newman run "${collection}" ` +
    `--environment "${envFile}" ` +
    `--env-var "baseUrl=${process.env.BASE_URL}" ` +
    `--env-var "accessToken=${process.env.ACCESS_TOKEN}" ` +
    `--reporters cli,allure ` +
    `--reporter-allure-export "${resultsDir}"`,
    { stdio: 'inherit' }
  );
  testsPassed = true;
} catch (e) {
  console.log('\n⚠️ Тесты завершились с ошибками, но результаты сохранены.');
}

// Создаём папку, если её нет
if (!fs.existsSync(resultsDir)) {
  fs.mkdirSync(resultsDir, { recursive: true });
}

// === Генерируем отчёт ===
console.log('\n📊 Генерация Allure-отчёта...');

if (fs.existsSync(reportDir)) {
  fs.rmSync(reportDir, { recursive: true, force: true });
}

try {
  execSync(`allure generate "${resultsDir}" -o "${reportDir}" --clean`, {
    stdio: 'inherit'
  });

  const reportPath = path.join(reportDir, 'index.html');
  console.log(`\n📄 Отчёт сохранён: file://${reportPath}`);

  // Попробовать открыть
  if (process.platform === 'win32') {
    try {
      execSync(`start "" "${reportPath}"`, { shell: true });
    } catch (err) {
      console.log('ℹ️ Не удалось открыть браузер. Открой вручную.');
    }
  }
} catch (err) {
  console.error('\n❌ Не удалось сгенерировать Allure-отчёт.');
  console.error('   Убедись, что установлен Allure CLI: npm install -g allure-commandline');
  console.error('   И что папка allure-results не пуста.');
}
// После генерации отчёта
console.log('\n🚀 Запуск локального сервера для Allure...');
execSync('npx serve -s allure-report', { stdio: 'inherit' });