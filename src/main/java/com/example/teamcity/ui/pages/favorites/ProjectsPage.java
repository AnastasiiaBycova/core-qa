package com.example.teamcity.ui.pages.favorites;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.selector.ByAttribute;
import com.example.teamcity.ui.elements.ProjectElement;

import java.util.List;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.element;
import static com.codeborne.selenide.Selenide.elements;

public class ProjectsPage extends FavoritesPage {

    private static final String FAVORITE_PROJECTS_URL = "/favorite/projects";
    private ElementsCollection subprojects = elements(new ByAttribute("data-test-itemtype", "project"));

    private SelenideElement createNewProjectButton = element(new ByAttribute("data-hint-container-id", "project-create-entity"));

    private ElementsCollection subBuildConfig = elements(new ByAttribute("data-test-itemtype", "buildType"));



    // ElementsCollection -> List<ProjectElement>
    public ProjectsPage open() {
        Selenide.open(FAVORITE_PROJECTS_URL);
        waitUntilFavoritePageIsLoaded();
        return this;
    }

    public List<ProjectElement> getSubprojects() {
            return generatePageElements(subprojects, ProjectElement::new);
    }

    public List<ProjectElement> getSubBuildConfig() {
        return generatePageElements(subBuildConfig, ProjectElement::new);
    }

    public void verifyCreateNewProjectButtonNotVisibilityAndNotExist() {
        createNewProjectButton.shouldNotBe(visible).shouldNot(exist);
    }
}