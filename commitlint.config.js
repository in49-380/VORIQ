// commitlint.config.js
module.exports = {
  extends: ['@commitlint/config-conventional'],
  rules: {
    // Пример: разрешить только указанные типы
    'type-enum': [
      2, // 2 = error
      'always',
      ['feature', 'bugfix', 'hotfix', 'refactor', 'test', 'docs', 'chore']
    ],
    // Обязательное наличие номера задачи вида #123
    'subject-full-stop': [0, 'never'], // не требовать точки в конце
    'subject-case': [0, 'never'],      // не требовать определённого регистра
    // Кастомное правило: subject должен содержать #<число>
    'header-pattern': [
      2,
      'always',
      /^(\w+): #(\d+) (.+)$/
    ],
    'header-pattern-flags': [2, 'always', ''],
  }
};