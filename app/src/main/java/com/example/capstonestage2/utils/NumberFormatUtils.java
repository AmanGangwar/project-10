package com.example.capstonestage2.utils;

public class NumberFormatUtils {

    public static String format2Decimal(double price) {
        String formattedNum = String.format("%.2f", price);
        return formattedNum.replace(".", ",");
    }
}
