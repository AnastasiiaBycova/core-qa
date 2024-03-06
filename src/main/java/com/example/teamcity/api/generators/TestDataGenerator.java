package com.example.teamcity.api.generators;

import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.NewProjectDescription;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.Role;
import com.example.teamcity.api.models.Roles;
import com.example.teamcity.api.models.User;

import java.util.Arrays;

public class TestDataGenerator {
    public static TestData generate() {
        var user = User.builder()
                .username(RandomData.getString())
                .password(RandomData.getString())
                .email(RandomData.getString() + "@gmail.com")
                .roles(Roles.builder()
                        .role(Arrays.asList(Role.builder()
                                                .roleId("SYSTEM_ADMIN")
                                                .scope("g")
                                        .build()))
                        .build())
                .build();

        var project = NewProjectDescription
                .builder()
                .parentProject(Project.builder()
                        .locator("_Root")
                        .build())
                .name(RandomData.getString())
                .id(RandomData.getString())
                .copyAllAssociatedSettings(true)
                .build();

        var buildType = BuildType.builder()
                .id(RandomData.getString())
                .name(RandomData.getString())
                .project(project)
                .build();

        return TestData.builder()
                .user(user)
                .project(project)
                .buildType(buildType)
                .build();
    }

    public static Roles generateRoles(com.example.teamcity.api.enums.Role role, String scope) {
        return (Roles.builder().role
                (Arrays.asList(Role.builder().roleId(role.getText())
                        .scope(scope).build())).build());
    }

    public static TestData generateInvalidData() {

        var projectWithWrongData = NewProjectDescription
                .builder()
                .parentProject(Project.builder()
                        .locator("_Root")
                        .build())
                .name(String.valueOf(RandomData.getRandomInt()))
                .id(String.valueOf(RandomData.getRandomInt()))
                .copyAllAssociatedSettings(true)
                .build();

        return TestData.builder()
                .project(projectWithWrongData)
                .build();
    }

    public static TestData generateEmptyData() {

        var project = NewProjectDescription
                .builder()
                .parentProject(Project.builder()
                        .locator("_Root")
                        .build())
                .name(RandomData.getString())
                .id(RandomData.getString())
                .copyAllAssociatedSettings(true)
                .build();

        var buildTypeWithEmptyData = BuildType.builder()
                .id(RandomData.getString())
                .name(RandomData.getEmpty())
                .project(project)
                .build();

        return TestData.builder()
                .project(project)
                .buildType(buildTypeWithEmptyData)
                .build();
    }
}
