package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.selector.ByAttribute;
import com.example.teamcity.ui.Selectors;
import com.example.teamcity.ui.pages.Page;

import static com.codeborne.selenide.Selenide.element;

public class CreateNewBuildConfig extends Page {

    private SelenideElement urlInput = element(Selectors.byId("url"));

    private SelenideElement buildConfigNameInput = element(Selectors.byId("buildTypeName"));

    private SelenideElement defaultBranchInput = element(Selectors.byId("branch"));

    private SelenideElement buildConfigurationIDInput = element(Selectors.byId("buildTypeExternalId"));

    private SelenideElement manuallyOptionButton = element(new ByAttribute("data-hint-container-id", "create-build-configuration"));

    private SelenideElement errorMessage = element(Selectors.byId("error_buildTypeExternalId"));


    public CreateNewBuildConfig createBuildConfigByUrl(String url) {
        urlInput.sendKeys(url);
        submit();
        waitUntilCreationPageIsLoaded();
        return this;
    }

    public void setupBuildConfig(String buildConfigName, String defaultBranch) {
        buildConfigNameInput.clear();
        buildConfigNameInput.sendKeys(buildConfigName);
        defaultBranchInput.clear();
        defaultBranchInput.sendKeys(defaultBranch);
        submit();
    }

    public void createBuildConfigManually(String buildConfigName, String buildConfigurationID) {
        manuallyOptionButton.click();
        buildConfigNameInput.clear();
        buildConfigNameInput.sendKeys(buildConfigName);
        buildConfigurationIDInput.clear();
        buildConfigurationIDInput.sendKeys(buildConfigurationID);
        submit();
    }
    public boolean errorMessageVisible(String expectedText) {
        if (errorMessage.exists()) {
            String actualText = errorMessage.getText();
            return actualText.equals(expectedText);
        } else {
            return false;
        }
    }
}
