Status of Last Deployment:<br>
<img src="https://github.com/AnastasiiaBycova/core-qa/workflows/TeamCity-Tests/badge.svg?branch=master"><br>

## DEMO построения проекта по автоматизации тестирования с 0.
Для проекта написан Тестовый план по автоматизации:
поэтапный план внедрения, метрики, роли, ресурсы и риски.

#### Тестируемый продукт - TeamCity CI (Jetbrains).

#### Стек: Java 17 (maven), TestNG, Rest Assured, Selenide, Lombok, Allure
Паттерны: Page Object, Page Element, Builder, Factory.



Реализовано два пака тестов:
### UI_e2e_regress, 
### API_regress.

Для тестирования на локальной машине под windows необходим запущенный проект на http://localhost:8111/
Для тестирования необходимо запустить-настроить docker-контейнеры:
- teamcity-server
- teamcity-agent
- selenoid
- selenoid-ui

Для автоматического поднятия приложения и запуска тестов написан shell скрипт:
### ../infra/setup_infra.sh.

На каждый коммит запускается пайплайн (сборка приложения на ubuntu и запуск автотестов)
Файл пайплайна: 
### ../.github/workflows/test.yml

test

