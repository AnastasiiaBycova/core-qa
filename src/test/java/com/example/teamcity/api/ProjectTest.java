package com.example.teamcity.api;

import com.example.teamcity.api.generators.TestDataGenerator;
import com.example.teamcity.api.requests.checked.CheckedProject;
import com.example.teamcity.api.requests.checked.CheckedUser;
import com.example.teamcity.api.requests.unchecked.UncheckedProject;
import com.example.teamcity.api.spec.Specifications;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

public class ProjectTest extends BaseApiTest {


    /*
    ### 1. ПОЗИТИВНЫЙ ТЕСТ "Успешное создание проекта"
    1. Создать проект с заполнением всех обязательных полей
    2. Проверка, что проект создался
    */

    @Test
    public void successfulCreatProject() {
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getUserRequest()
                .create(testData.getUser());

        new UncheckedProject(Specifications.getSpec().authSpec(testData.getUser()))
                .create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_OK);

        new UncheckedProject(Specifications.getSpec().authSpec(testData.getUser()))
                .get(testData.getProject().getId())
                .then().assertThat().statusCode(HttpStatus.SC_OK);

    }

    /*
    ### 2. НЕГАТИВНЫЙ ТЕСТ "Проект не создается при заполнении полей неправильными данными"
    1. Создать проект с заполнением полей неправильными данными
    2. Проверка сообщения об ошибке и статус кода
    3. Запросить создаваемы проект - проверка, что такого проекта не существует
    */
    @Test
    public void failCreatProjectWithWrongData() {
        var userTestData = testDataStorage.addTestData();
        var projectTestData = TestDataGenerator.generateInvalidData();

        checkedWithSuperUser.getUserRequest()
                .create(userTestData.getUser());

        new UncheckedProject(Specifications.getSpec().authSpec(userTestData.getUser()))
                .create(projectTestData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .body(Matchers.containsString("ID should start with a latin letter and contain only latin letters"));

        new UncheckedProject(Specifications.getSpec().authSpec(userTestData.getUser()))
                .get(projectTestData.getProject().getId())
                .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND);
    }

        /*
    ### 3. НЕГАТИВНЫЙ ТЕСТ "Проверка уникальности имени и id проекта. Создание проекта с уже существующим id и именем проекта невозможно"
    1. Создать первый проект
    2. Создать второй проект с именем и id первого проекта.
    3. Проверка статуса кода и сообщения об ошибке

    */

    @Test
    public void createProjectWithUniqueId() {
        var testData = testDataStorage.addTestData();

        checkedWithSuperUser.getUserRequest()
                .create(testData.getUser());

        new UncheckedProject(Specifications.getSpec().authSpec(testData.getUser()))
                .create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_OK);

        new UncheckedProject(Specifications.getSpec().authSpec(testData.getUser()))
                .create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("Project with this name already exists: " + testData.getProject().getName()));

    }


}
