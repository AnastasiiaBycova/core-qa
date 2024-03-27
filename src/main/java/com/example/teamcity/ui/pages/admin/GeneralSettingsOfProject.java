package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.Selectors;
import com.example.teamcity.ui.pages.Page;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.element;

public class GeneralSettingsOfProject extends Page {

    private SelenideElement closeButton = element(Selectors.byClass("btn cancel"));

    private SelenideElement successMessage = element(Selectors.byClass("successMessage"));

    private SelenideElement createBuildConfigButton = $(byText("Create build configuration"));


    public GeneralSettingsOfProject openEditProject(String ProjectId) {
        Selenide.open("/admin/editProject.html?projectId=" + ProjectId);
        waitUntilPageIsLoaded();
        waitUntilCreationPageIsLoaded();
        return this;
    }
    public GeneralSettingsOfProject close(String parentProjectId) {
        closeButton.click();
        return this;
    }

    public boolean successMessageVisible(String expectedText) {
        if (successMessage.exists()) {
            String actualText = successMessage.getText();
            return actualText.equals(expectedText);
        } else {
            return false;
        }
    }

    public GeneralSettingsOfProject createBuildConfig() {
        createBuildConfigButton.scrollIntoView(true).click();
        waitUntilPageIsLoaded();
        return this;
    }
    public void verifyBuildConfigVisibilityInTable(String buildConfigName) {
        SelenideElement container = $(By.id("configurations"));
        SelenideElement element = container.$(By.xpath(".//*[text()='" + buildConfigName + "']"));
        element.shouldBe(visible);
    }


}
