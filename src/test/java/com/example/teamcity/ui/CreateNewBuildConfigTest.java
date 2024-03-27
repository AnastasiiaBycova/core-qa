package com.example.teamcity.ui;

import com.example.teamcity.api.generators.RandomData;
import com.example.teamcity.api.requests.unchecked.UncheckedBuildConfig;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.ui.pages.admin.CreateNewBuildConfig;
import com.example.teamcity.ui.pages.admin.GeneralSettingsOfProject;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import java.util.List;

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
     * 8. Проверка, что билд-конфигурация создана - отображается в таблице билд-конфигураций проекта
     * 9. Проверка, что билд-конфигурация создана - API запрос
     * 10. --Очистка данных--
     */
    @Test
    public void authorizedUserShouldBeAbleCreateNewBuildConfigByUrl() {

        var testData = testDataStorage.addTestDataForUITest();
        var url = "https://github.com/AnastasiiaBycova/core-qa";
        var defaultBranch = RandomData.getString();
        var textOfSuccessCreateBuildConfig =
                String.format("New build configuration \"%s\" and VCS root %s#%s have been successfully created.", testData.getBuildType().getName(), url, defaultBranch);

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

        new UncheckedBuildConfig(Specifications.getSpec().authSpec(testData.getUser()))
                .get(testData.getBuildType().getId())
                .then().assertThat().statusCode(HttpStatus.SC_OK);
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
     * 11. Проверка, что вторая билд-конфигурация не создана - API запрос (в списке конфигурация должна быть только одна конфигурация с заданным именем)
     * 12. --Очистка данных--
     */

    @Test
    public void authorizedUserShouldNotBeAbleCreateTwoEqualBuildConfigInProject() {

        var testData = testDataStorage.addTestDataForUITest();
        String testBuildName = testData.getBuildType().getName();
        var textOfFailCreateBuildConfig =
               "The build configuration / template ID \"" + testData.getBuildType().getId() + "\" is already used by another configuration or template";

        checkedWithSuperUser.getProjectRequest().create(testData.getProject());
        loginAsUser(testData.getUser());

        generalSettingsOfProject
                .openEditProject(testData.getProject().getId())
                .createBuildConfig();

        createNewBuildConfig
                .createBuildConfigManually(testBuildName, testData.getBuildType().getId());

        generalSettingsOfProject
                .openEditProject(testData.getProject().getId())
                .createBuildConfig();

        createNewBuildConfig
                .createBuildConfigManually(testBuildName, testData.getBuildType().getId());

        createNewBuildConfig
                .errorMessageVisible(textOfFailCreateBuildConfig);

        // Проверка на уровне API:
        var responseBuildConfig = new UncheckedBuildConfig(Specifications.getSpec().authSpec(testData.getUser()))
                .get();

        List<String> buildNames = responseBuildConfig.jsonPath().getList("buildType.name");

        if (buildNames.stream().filter(name -> name.equals(testBuildName)).count() != 1) {
            // Если количество билдов с именем не равно 1, то генерируем ошибку
            throw new AssertionError("Expected exactly one build configuration with name: " + testBuildName);
        }
    }
}