package com.example.teamcity.api.generators;

import java.util.ArrayList;
import java.util.List;

public class TestDataStorage {
    private static TestDataStorage testDataStorage;
    private List<TestData> testDataList;                // список тестовых данных

    private TestDataStorage() {                         // при первом вызове необх создать пустой лист
        this.testDataList = new ArrayList<>();
    }

    public static TestDataStorage getStorage() {        // метод, который дает доступ к TestDataStorage
        if (testDataStorage == null) {
            testDataStorage = new TestDataStorage();
        }
        return testDataStorage;
    }

    public TestData addTestData() {                 // генерируем с нуля и добавляем новую
        var testData = TestDataGenerator.generate();
        addTestData(testData);
        return testData;
    }

    public TestData addTestData(TestData testData) { // добавить ту, которую передаем
        getStorage().testDataList.add(testData);
        return testData;
    }

    public void delete() {
        testDataList.forEach(TestData::delete);
    }
}
