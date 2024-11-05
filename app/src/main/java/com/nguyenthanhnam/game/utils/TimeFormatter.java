package com.nguyenthanhnam.game.utils;

import java.util.Locale;

public class TimeFormatter {
    public static String formatTime(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) (milliseconds / 1000) / 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}