package com.example.teamcity.ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.Selectors;
import com.example.teamcity.ui.elements.PageElement;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.codeborne.selenide.Selenide.element;

public abstract class Page {
    protected SelenideElement submitButton = element(Selectors.byType("submit"));
    private SelenideElement savingWaitingMarker = element(Selectors.byId("saving"));
    private SelenideElement pageWaitingMarker1 = element(Selectors.byDataTest("ring-loader"));

    private SelenideElement pageWaitingMarker2 = element(Selectors.byId("inProgress"));

    private SelenideElement pageCreateWaitingMarker = element(Selectors.byClass("icon-refresh icon-spin ring-loader-inline progressRing progressRingDefault"));

    //waiting сохранения данных
    public void submit() {
        submitButton.click();
        waitUntilDataIsSaved();
    }

    //waiting загрузки страниц
    public void waitUntilPageIsLoaded() {
        pageWaitingMarker1.shouldNotBe(Condition.visible, Duration.ofMinutes(1));
        pageWaitingMarker2.shouldNotBe(Condition.visible, Duration.ofMinutes(1));
    }

    public void waitUntilDataIsSaved() {
        savingWaitingMarker.shouldNotBe(Condition.visible, Duration.ofSeconds(30));
    }

    public void waitUntilCreationPageIsLoaded() {
        pageCreateWaitingMarker.shouldNotBe(Condition.visible, Duration.ofSeconds(30));
    }

    /*
    метод, который возвращает List<ProjectElement>
    и вызовем метод generatePageElements на коллекции,
    которую мы спарсили subprojects
    */

    // по каждому webElement создаем pageElement. и созданный pageElement добавляем в коллекцию elements
    // Creator - РЕАЛИЗАЦИЯ ПАТТЕРНА - ФАБРИКА
    public  <T extends PageElement> List<T> generatePageElements(
            ElementsCollection collection,
            Function<SelenideElement, T> creator) {
        var elements = new ArrayList<T>();
        collection.forEach(webElement -> elements.add(creator.apply(webElement)));
        return elements;
    }

}
