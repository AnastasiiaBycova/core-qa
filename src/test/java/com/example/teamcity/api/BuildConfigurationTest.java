package com.example.teamcity.api;


import com.example.teamcity.api.generators.TestDataGenerator;
import com.example.teamcity.api.requests.checked.CheckedProject;
import com.example.teamcity.api.requests.checked.CheckedUser;
import com.example.teamcity.api.requests.unchecked.UncheckedBuildConfig;
import com.example.teamcity.api.requests.unchecked.UncheckedProject;
import com.example.teamcity.api.spec.Specifications;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

// 0.
public class BuildConfigurationTest extends BaseApiTest {
    @Test
    public void buildConfigurationTest() {

        var testData = testDataStorage.addTestData();

        new CheckedUser(Specifications.getSpec().superUserSpec())
                .create(testData.getUser());

        var project = new CheckedProject(Specifications.getSpec()
                .authSpec(testData.getUser()))
                .create(testData.getProject());

        softy.assertThat(project.getId()).isEqualTo(testData.getProject().getId());
    }


    /*
    ### 1. ПОЗИТИВНЫЙ ТЕСТ "Успешное создание билд конфигурации"
    1. Создать проект
    2. Создать билд-конфигурацию с заполненем валидными данными всех обязательных полей
    3. Проверка, что билд конфигурация создана
    */
    @Test
    public void successfulCreatBuildConfig() {
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getUserRequest()
                .create(testData.getUser());

        new UncheckedProject(Specifications.getSpec().authSpec(testData.getUser()))
                .create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_OK);

        new UncheckedBuildConfig(Specifications.getSpec().authSpec(testData.getUser()))
                .create(testData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_OK);

        new UncheckedBuildConfig(Specifications.getSpec().authSpec(testData.getUser()))
                .get(testData.getBuildType().getId())
                .then().assertThat().statusCode(HttpStatus.SC_OK);

    }
    /*
    ### 2. ПОЗИТИВНЫЙ ТЕСТ "Успешное создание билд конфигурации после удаления билд конфигурации с теми же данными"
    1. Создать проект
    2. Создать билд конфигурацию-1
    3. Удалить билд конфигурацию-1
    3. Создать билд конфигурацию-2 с данными билд конфигурации-1
    Проверка, что билд конфигурация-2 успешно создана
    */
    @Test
    public void successfulCreatBuildConfigAfterRemoveSameBuildConfig() {
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getUserRequest()
                .create(testData.getUser());

        new UncheckedProject(Specifications.getSpec().authSpec(testData.getUser()))
                .create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_OK);

        new UncheckedBuildConfig(Specifications.getSpec().authSpec(testData.getUser()))
                .create(testData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_OK);

        new UncheckedBuildConfig(Specifications.getSpec().authSpec(testData.getUser()))
                .delete(testData.getBuildType().getId())
                .then().assertThat().statusCode(HttpStatus.SC_NO_CONTENT);

        new UncheckedBuildConfig(Specifications.getSpec().authSpec(testData.getUser()))
                .create(testData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_OK);

        new UncheckedBuildConfig(Specifications.getSpec().authSpec(testData.getUser()))
                .get(testData.getBuildType().getId())
                .then().assertThat().statusCode(HttpStatus.SC_OK);

    }

    /*
   ### 3. НЕГАТИВНЫЙ ТЕСТ "Нельзя создать билд-конфигурацию в несуществующем проекте"
   1. Создать проект
   2. Создать билд-конфигурацию с другим id проекта
   Проверка статус кода ответа и сообщения об ошибке.
   3. Проверка, что билд конфигурация НЕ создана
   */
    @Test
    public void failCreatBuildConfigWithoutParentProject() {
        var userAndProjectData = testDataStorage.addTestData();
        var buildConfigTestData = TestDataGenerator.generate();
        checkedWithSuperUser.getUserRequest()
                .create(userAndProjectData.getUser());

        new UncheckedProject(Specifications.getSpec().authSpec(userAndProjectData.getUser()))
                .create(userAndProjectData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_OK);

        new UncheckedBuildConfig(Specifications.getSpec().authSpec(userAndProjectData.getUser()))
                .create(buildConfigTestData.getBuildType())
                .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND)
                .body(Matchers.containsString("Project cannot be found by external id"));

        new UncheckedBuildConfig(Specifications.getSpec().authSpec(userAndProjectData.getUser()))
                .get(buildConfigTestData.getBuildType().getId())
                .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND);

    }


}
