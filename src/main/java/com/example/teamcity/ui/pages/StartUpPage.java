package com.example.teamcity.ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.Selectors;
import lombok.Getter;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.element;

public class StartUpPage extends Page {

    @Getter
    private SelenideElement header = element(Selectors.byId("header"));

    private SelenideElement proceedButton = element(Selectors.byId("proceedButton"));

    private SelenideElement acceptLicense = element(Selectors.byId("accept"));


    public StartUpPage open() {
        Selenide.open("/");
        return this;
    }

    public StartUpPage setupTeamCityServer() {
        waitUntilPageIsLoaded();
        proceedButton.click();
        waitUntilPageIsLoaded();
        proceedButton.click();
        waitUntilPageIsLoaded();
        acceptLicense.shouldBe(Condition.enabled, Duration.ofMinutes(5));
        acceptLicense.scrollTo();
        acceptLicense.click();
        submitButton.click();
        return this;
    }
}
