package com.example.teamcity.api.generators;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

public class RandomData {
    private static final int LENGTH = 10;

    public static String getString() {
        return "test_" + RandomStringUtils.randomAlphabetic(LENGTH);
    }

    public static String getEmpty() {
        return "";
    }

    public static char[] getRandomInt() {
        Random random = new Random();
        int randomNumber = random.nextInt(101); // Генерация случайного числа от 0 до 100
        return String.valueOf(randomNumber).toCharArray();
    }
}
