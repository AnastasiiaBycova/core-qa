package com.example.teamcity.ui;

import com.codeborne.selenide.Condition;
import com.example.teamcity.api.enums.Role;
import com.example.teamcity.api.generators.TestDataGenerator;
import com.example.teamcity.ui.elements.HeaderElement;
import com.example.teamcity.ui.pages.LoginPage;
import com.example.teamcity.ui.pages.admin.CreateNewBuildConfig;
import com.example.teamcity.ui.pages.admin.CreateNewProject;
import com.example.teamcity.ui.pages.admin.GeneralSettingsOfProject;
import com.example.teamcity.ui.pages.favorites.ProjectsPage;
import org.testng.annotations.Test;

public class CreateNewProjectTest extends BaseUiTest {

    CreateNewProject createNewProject = new CreateNewProject();
    ProjectsPage projectsPage = new ProjectsPage();
    GeneralSettingsOfProject generalSettingsOfProject = new GeneralSettingsOfProject();


/**
### 1. ПОЗИТИВНЫЙ ТЕСТ "Авторизованный юзер может создать проект из URL адреса репозитория."
1. Создать юзера
2. Авторизоваться под этим юзером
3. Открыть страницу создания проекта
4. Перейти на страницу создания проекта из URL адреса репозитория
5. Заполнить поле Repository URL → Нажать "Proceed"
6. Заполнить обязательные поля (Project Name, Build Configuration Name) → → Нажать "Proceed"
7. Перейти на страницу всех проектов
8. Проверка, что проект создан и отображается в дереве проектов на первом месте
9. --Очистка данных--
*/
    @Test
    public void authorizedUserShouldBeAbleCreateNewProjectByUrl() {
        var testData = testDataStorage.addTestDataForUITest();
        var url = "https://github.com/AnastasiiaBycova/core-qa";

        loginAsUser(testData.getUser());

        createNewProject
                .open(testData.getProject().getParentProject().getLocator())
                .createProjectByUrl(url)
                .setupProject(testData.getProject().getName(), testData.getBuildType().getName());

        projectsPage.open()
                .getSubprojects()
                .stream().reduce((first, second) -> first).get()
                .getHeader().shouldHave(Condition.text(testData.getProject().getName()));
    }

    /**
     ### 2. ПОЗИТИВНЫЙ ТЕСТ "Авторизованный юзер может создать проект Manually."
     1. Создать юзера
     2. Авторизоваться под этим юзером
     3. Открыть страницу создания проекта
     4. Перейти на страницу создания проекта Manually
     5. Заполнить поле "Name" → Нажать "Create"
     6. Проверка, что отображается сообщение об успешном создании проекта "Project -projectName- has been successfully created."
     7. Перейти на страницу всех проектов
     8. Проверка, что проект создан и отображается в дереве проектов
     9. --Очистка данных--
     */
    @Test
    public void authorizedUserShouldBeAbleCreateNewProjectManually() {
        var testData = testDataStorage.addTestDataForUITest();
        var textOfSuccessCreateProject = "Project " + testData.getProject().getName() + " has been successfully created. You can now create a build configuration.";

        loginAsUser(testData.getUser());

        createNewProject
                .open(testData.getProject().getParentProject().getLocator())
                .createProjectManually(testData.getProject().getName());

        generalSettingsOfProject
                .successMessageVisible(textOfSuccessCreateProject);

        projectsPage.open()
                .getSubprojects()
                .stream().reduce((first, second) -> first).get()
                .getHeader().shouldHave(Condition.text(testData.getProject().getName()));
    }

    /**
    ### 3. НЕГАТИВНЫЙ ТЕСТ "Авторизованный юзер с правами PROJECT_VIEWER не может создать проект"
     1. Создать юзера с правами PROJECT_VIEWER и соответстующий проект
     2. Авторизоваться под этим юзером
     3. Проверка, что рядом с разделом Projects в главном меню нет кнопки +
     4. Перейти на страницу проектов
     5. Проверка, что на странице проектов отсутствует кнопка "New project"
     6. --Очистка данных--
    */

    @Test
    public void authorizedUserWithRightProjectViewerShouldNotBeAbleCreateNewProject() {
        var testData = testDataStorage.addTestDataForUITest();

        checkedWithSuperUser.getProjectRequest().create(testData.getProject());

        testData.getUser().setRoles(TestDataGenerator.
                generateRoles(Role.PROJECT_VIEWER, "p:" + testData.getProject().getId()));

        checkedWithSuperUser.getUserRequest().create(testData.getUser());

        new LoginPage().open().login(testData.getUser());

        new HeaderElement(null)
                .verifyAddNewProjectButtonNotVisibilityAndNotExist();

        projectsPage
                .open()
                .verifyCreateNewProjectButtonNotVisibilityAndNotExist();
    }
}
