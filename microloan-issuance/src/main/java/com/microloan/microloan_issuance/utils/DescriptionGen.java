package com.microloan.microloan_issuance.utils;

import java.util.Random;

public class DescriptionGen {
    public static String generateRandomDescription(int count) {
        String chars = "абвгдежзийклмнопрстуфхцчшщъыьэюяАБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        int length = 20 + random.nextInt(count); // от 20 до count символов
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
