package com.example.teamcity.ui;

import com.codeborne.selenide.Condition;
import com.example.teamcity.api.generators.RandomData;
import com.example.teamcity.ui.pages.admin.CreateNewBuildConfig;
import com.example.teamcity.ui.pages.admin.CreateNewProject;
import com.example.teamcity.ui.pages.admin.GeneralSettingsOfProject;
import com.example.teamcity.ui.pages.favorites.ProjectsPage;
import org.testng.annotations.Test;

public class CreateNewBuildConfigTest extends BaseUiTest {

    GeneralSettingsOfProject generalSettingsOfProject = new GeneralSettingsOfProject();
    CreateNewBuildConfig createNewBuildConfig = new CreateNewBuildConfig();

    /**
     * ### 1. ПОЗИТИВНЫЙ ТЕСТ "Авторизованный юзер может создать билд конфигурацию в проекте"
     * Предусловия:
     * 1. Создать юзера
     * 2. Авторизоваться под этим юзером
     * 3. Создать проект
     * Тест:
     * 1. Перейти на страницу проектов
     * 2. Открыть страницу редактироания проекта → Перейти на страницу General Settings для проекта
     * 3. Нажать кнопку Create Build Configuration
     * 4. Ввести URL → Proceed
     * 5. Ввести имя билд-конфигурации → Proceed
     * 6. Проверка отображения сообщения "New build configuration have been successfully created."
     * 7. Перейти на страницу редактирования проекта
     * 8. Проверка, что билд-конфигурация создана и отображается в таблице билд-конфигураций проекта
     * 9. --Очистка данных--
     */
    @Test
    public void authorizedUserShouldBeAbleCreateNewBuildConfigByUrl() {

        var testData = testDataStorage.addTestDataForUITest();
        var url = "https://github.com/AnastasiiaBycova/core-qa";
        var defaultBranch = RandomData.getString();
        var textOfSuccessCreateBuildConfig =
                "New build configuration\"" + testData.getBuildType().getName() + "\" and VCS root " + url + '#' + defaultBranch + "\" have been successfully created.";

        checkedWithSuperUser.getProjectRequest().create(testData.getProject());
        loginAsUser(testData.getUser());

        generalSettingsOfProject
                .openEditProject(testData.getProject().getId())
                .createBuildConfig();

        createNewBuildConfig
                .createBuildConfigByUrl(url)
                .setupBuildConfig(testData.getBuildType().getName(), defaultBranch);

        generalSettingsOfProject
                .successMessageVisible(textOfSuccessCreateBuildConfig);

        generalSettingsOfProject
                .openEditProject(testData.getProject().getId())
                .verifyBuildConfigVisibilityInTable(testData.getBuildType().getName());
    }


    /**
     * ### 2. НЕГАТИВНЫЙ ТЕСТ "Авторизованный юзер не может создать две билд конфигурации в одном проекте с одинаковым build_ID"
     * Предусловия:
     * 1. Создать юзера
     * 2. Авторизоваться под этим юзером
     * 3. Создать проект
     * Тест:
     * 1. Перейти на страницу проектов
     * 2. Открыть страницу редактироания проекта → Перейти на страницу General Settings для проекта
     * 3. Нажать кнопку Create Build Configuration
     * 4. Перейти в создание build Config Manually
     * 5. Ввести Name → Ввести build Configuration ID → Create
     * 6. Открыть страницу редактироания проекта → Перейти на страницу General Settings для проекта
     * 7. Нажать кнопку Create Build Configuration
     * 8. Перейти в создание build Config Manually
     * 9. Ввести Name → Ввести build Configuration ID (такое же, как в п.5) → Create
     * 10. Проверка отображения сообщения ошибки "The build configuration / template ID "Test123_Testname2" is already used by another configuration or template"
     * 11. --Очистка данных--
     */

    @Test
    public void authorizedUserShouldNotBeAbleCreateTwoEqualBuildConfigInProject() {

        var testData = testDataStorage.addTestDataForUITest();
        var textOfFailCreateBuildConfig =
               "The build configuration / template ID \"" + testData.getBuildType().getId() + "\" is already used by another configuration or template";

        checkedWithSuperUser.getProjectRequest().create(testData.getProject());
        loginAsUser(testData.getUser());

        generalSettingsOfProject
                .openEditProject(testData.getProject().getId())
                .createBuildConfig();

        createNewBuildConfig
                .createBuildConfigManually(testData.getBuildType().getName(), testData.getBuildType().getId());

        generalSettingsOfProject
                .openEditProject(testData.getProject().getId())
                .createBuildConfig();

        createNewBuildConfig
                .createBuildConfigManually(testData.getBuildType().getName(), testData.getBuildType().getId());

        createNewBuildConfig
                .errorMessageVisible(textOfFailCreateBuildConfig);
    }
}