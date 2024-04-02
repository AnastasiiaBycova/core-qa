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

    private SelenideElement usernameInput = element(Selectors.byId("input_teamcityUsername"));
    private SelenideElement passwordInput = element(Selectors.byId("password1"));

    private SelenideElement confirmPasswordInput = element(Selectors.byId("retypedPassword"));


    public StartUpPage open() {
        Selenide.open("/mnt");
        return this;
    }

    public StartUpPage setupTeamCityServer() {
        waitUntilStartPageIsLoaded();
        proceedButton.click();
        waitUntilStartPageIsLoaded();
        proceedButton.click();
        waitUntilStartPageIsLoaded();
        acceptLicense.shouldBe(Condition.enabled, Duration.ofMinutes(5));
        acceptLicense.scrollTo();
        acceptLicense.click();
        submitButton.click();
        return this;
    }

    public StartUpPage createAdministratorAccount() {
        waitUntilStartPageIsLoaded();
        usernameInput.clear();
        usernameInput.sendKeys("admin");
        passwordInput.clear();
        passwordInput.sendKeys("admin");
        confirmPasswordInput.clear();
        confirmPasswordInput.sendKeys("admin");
        submit();
        return this;
    }
}
