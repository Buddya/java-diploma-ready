package ru.netology.graphics.image;

public class MyTextColorSchema implements TextColorSchema {
    private final char[] chars = new char[]{'#', '$', '@', '%', '*', '+', '-', '\''};

    @Override
    public char convert(int color) {
        return chars[color / 32];
    }
}
