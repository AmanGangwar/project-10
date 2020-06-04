package com.example.capstonestage2.utils;

import com.example.capstonestage2.activities.MainActivity;

public class PicassoUtils {

    public static String getFullCoinImageUrl(String part) {

        return MainActivity.CRYPTOCOMPARE_BASE_URL + part;
    }

    public static String getFullCoinImageUrlSmall(String part) {
        return getFullCoinImageUrl(part) + "?width=50";
    }
}
