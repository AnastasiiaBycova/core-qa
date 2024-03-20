package com.example.teamcity.ui.elements;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.selector.ByAttribute;
import lombok.Getter;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.element;

@Getter
public class HeaderElement extends PageElement {
    private SelenideElement addNewProjectButton = element(new ByAttribute("data-test-icon", "add"));

    public HeaderElement(SelenideElement element) {
        super(element);
    }

    public void verifyAddNewProjectButtonNotVisibilityAndNotExist() {
        addNewProjectButton.shouldNotBe(visible).shouldNot(exist);
    }
}
