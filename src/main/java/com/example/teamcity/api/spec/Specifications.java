package com.example.teamcity.api.spec;

import com.example.teamcity.api.config.Config;
import com.example.teamcity.api.models.User;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class Specifications {
    private static Specifications spec;                                        // переменная, в кот хранятся специф.

    private Specifications() {}                                         // сделали приватный конструктор и закрыли его,т.е. больше нельзя создать новый Specifications

    public static Specifications getSpec() {                            // забрать экземпляр спеки единственный. будет возвращать экземпляр, если он еще не создан
        if (spec == null) {
            spec = new Specifications();
        }
        return spec;
    }

    private RequestSpecBuilder reqBuilder() {
        var requestBuilder = new RequestSpecBuilder();
        requestBuilder.setBaseUri("http://192.168.0.103:8111");
        requestBuilder.addFilter(new RequestLoggingFilter());             // фильтр для логирования запросов
        requestBuilder.addFilter(new ResponseLoggingFilter());            // фильтр для логирования запросов
        requestBuilder.setContentType(ContentType.JSON);
        requestBuilder.setAccept(ContentType.JSON);
        return requestBuilder;
    }

    public RequestSpecification unauthSpec() {
        var requestBuilder = reqBuilder();        /* создаем спец. класс, который является строителем, в него накапливаем информацию и потом явно вызываем
        метод билд и только после этого нам возвращается объект с этими свойствами*/
        return requestBuilder.build();
    }

    public RequestSpecification authSpec(User user) {
        var requestBuilder = reqBuilder();
        requestBuilder.setBaseUri("http://"  + user.getUsername() + ":" + user.getPassword() + "@" + Config.getProperty("host") + ":" + Config.getProperty("port"));
        return requestBuilder.build();          // возвращаем то, что requestBuilder нам сбилдит
    }

    public RequestSpecification superUserSpec() {
        var requestBuilder = reqBuilder();
        requestBuilder.setBaseUri("http://" + Config.getProperty("superUserToken") + "@" + Config.getProperty("host")+ ":" + Config.getProperty("port"));
        return requestBuilder.build();
    }

}
