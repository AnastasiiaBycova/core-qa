package com.example.teamcity.api;

import com.example.teamcity.api.enums.Role;
import com.example.teamcity.api.generators.TestDataGenerator;
import com.example.teamcity.api.requests.checked.CheckedBuildConfig;
import com.example.teamcity.api.requests.checked.CheckedProject;
import com.example.teamcity.api.requests.unchecked.UncheckedBuildConfig;
import com.example.teamcity.api.spec.Specifications;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

public class RolesTest extends BaseApiTest{

    /* Тест 1: Права неавторизованного пользователя --------------------------------------------------------------------
    1) Создаем проект неавторизованным пользователем
    2) Проверяем корректность ошибки
    3) Проверяем несоздание сущности
    */
    @Test(groups = "API_Regress")
    public void unauthorizedUserShouldNotHaveRightToCreateProject() {
        var testData = testDataStorage.addTestData();

        uncheckedWithSuperUser.getProjectRequest()
                .create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body(Matchers.containsString("Authentication required"));

       uncheckedWithSuperUser.getProjectRequest()
                .get(testData.getProject().getId())
                .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND)
                .body(Matchers.containsString
                        ("No project found by name or internal/external id 'id" + testData.getProject().getId() + "'"));
    }


   /* Тест 2: Права пользователя SYSTEM_ADMIN --------------------------------------------------------------------------
    1) Логинимся под пользователем с правами SYSTEM_ADMIN
    2) Создаем проект
    3) Проверяем, что проект создался
    */
    @Test(groups = "API_Regress")
    public void systemAdminShouldHaveRightsToCreateProject() {
        var testData = testDataStorage.addTestData();

        testData.getUser().setRoles(TestDataGenerator.generateRoles(Role.SYSTEM_ADMIN, "g"));

        checkedWithSuperUser.getUserRequest()
                .create(testData.getUser());

        var project = new CheckedProject(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getProject());

        softy.assertThat(project.getId()).isEqualTo(testData.getProject().getId());
    }

    /* Тест 3: Права пользователя PROJECT_ADMIN ------------------------------------------------------------------------

       ПОЗИТИВНЫЙ КЕЙС:
    1) Логинимся под пользователем с правами PROJECT_ADMIN
    2) Создаем конфигурацию для проекта, где он PROJECT_ADMIN
    3) Проверяем, что конфигурация создалась
    */
    @Test(groups = "API_Regress")
    public void projectAdminShouldHaveRightsToCreateBuildConfigToHisProject() {
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getProjectRequest()
                .create(testData.getProject());

        testData.getUser().setRoles(TestDataGenerator.
                generateRoles(Role.PROJECT_ADMIN, "p:" + testData.getProject().getId()));

        checkedWithSuperUser.getUserRequest()
                .create(testData.getUser());

        var buildConfig = new CheckedBuildConfig(Specifications.
                getSpec().authSpec(testData.getUser()))
                .create(testData.getBuildType());
        // проверка
        softy.assertThat(buildConfig.getId()).isEqualTo(testData.getBuildType().getId());
    }

    /* НЕГАТИВНЫЙ КЕЙС:
    (необходимо 2 юзера.
     первый - создает проект для себя, второй - также для себя, затем пытаться изменить конфигурацию первого юзера)
    1) Логинимся под пользователем с правами PROJECT_ADMIN
    2) Создаем конфигурацию для проекта, где он НЕ PROJECT_ADMIN
    3) Проверяем, что конфигурация НЕ создалась
    */
    @Test(groups = "API_Regress")
    public void projectAdminShouldNotHaveRightsToCreateBuildConfigToAnotherProject() {
        var firstTestData = testDataStorage.addTestData();
        var secondTestData = testDataStorage.addTestData();

        checkedWithSuperUser.getProjectRequest().create(firstTestData.getProject());
        checkedWithSuperUser.getProjectRequest().create(secondTestData.getProject());

        firstTestData.getUser().setRoles(TestDataGenerator
                .generateRoles(Role.PROJECT_ADMIN, "p:" + firstTestData.getProject().getId()));

        checkedWithSuperUser.getUserRequest()
                .create(firstTestData.getUser());

        secondTestData.getUser().setRoles(TestDataGenerator.
                generateRoles(Role.PROJECT_ADMIN, "p:" + secondTestData.getProject().getId()));

        checkedWithSuperUser.getUserRequest()
                .create(secondTestData.getUser());

        new UncheckedBuildConfig(Specifications.getSpec().authSpec(secondTestData.getUser()))
                .create(firstTestData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST);         // есть дефект,тк приходит статус 200
    }

    /* Тест 4: Права пользователя PROJECT_VIEWER ------------------------------------------------------------------------

       ПОЗИТИВНЫЙ КЕЙС:
    1) Под суперюзером создаем проект
    2) Создаем PROJECT_ADMIN для этого проекта
    3) Логинимся под пользователем с правами PROJECT_ADMIN
    4) Создаем билд конфигурацию для проекта
    5) Создаем PROJECT_VIEWER
    6) Логинимся под пользователем с правами PROJECT_VIEWER
    7) Проверяем, что PROJECT_VIEWER видит созданную билд конфигурацию
    */
    @Test(groups = "API_Regress")
    public void projectViewerShouldHaveRightsToReadBuildConfigToHisProject() {
        var firstTestData = testDataStorage.addTestData();
        var secondTestData = testDataStorage.addTestData();

        checkedWithSuperUser.getProjectRequest().create(firstTestData.getProject());

        firstTestData.getUser().setRoles(TestDataGenerator.
                generateRoles(Role.PROJECT_ADMIN, "p:" + firstTestData.getProject().getId()));

        checkedWithSuperUser.getUserRequest().create(firstTestData.getUser());

        new UncheckedBuildConfig(Specifications.getSpec().authSpec(firstTestData.getUser()))
                .create(firstTestData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_OK);

        secondTestData.getUser().setRoles(TestDataGenerator.
                generateRoles(Role.PROJECT_VIEWER, "p:" + firstTestData.getProject().getId()));

        checkedWithSuperUser.getUserRequest().create(secondTestData.getUser());

        var BuildConfig = new UncheckedBuildConfig(Specifications.
                getSpec().authSpec(secondTestData.getUser()))
                .get(firstTestData.getBuildType().getId())
                .then().assertThat().statusCode(HttpStatus.SC_OK);

        // в конце теста заходим под суперюзером, чтобы удалить созданные в тесте данные
        checkedWithSuperUser.getProjectRequest().get(firstTestData.getProject().getId());

    }

    /*
    НЕГАТИВНЫЙ КЕЙС:
    1) Под суперюзером создаем проект
    2) Создаем PROJECT_VIEWER
    3) Логинимся под пользователем с правами PROJECT_VIEWER
    4) Создаем билд конфигурацию для проекта
    5) Проверяем, что PROJECT_VIEWER видит созданную билд конфигурацию
    */
    @Test(groups = "API_Regress")
    public void projectViewerShouldNotHaveRightsToCreateBuildConfigToHisProject() {
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getProjectRequest().create(testData.getProject());

        testData.getUser().setRoles(TestDataGenerator.
                generateRoles(Role.PROJECT_VIEWER, "p:" + testData.getProject().getId()));

        checkedWithSuperUser.getUserRequest().create(testData.getUser());

        new UncheckedBuildConfig(Specifications.getSpec().authSpec(testData.getUser()))
                .create(testData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_FORBIDDEN)
                .body(Matchers.containsString("Access denied"));;

        // в конце теста заходим под суперюзером, чтобы удалить созданные в тесте данные
        checkedWithSuperUser.getProjectRequest().get(testData.getProject().getId());
    }
}
