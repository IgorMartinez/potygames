package br.com.igormartinez.potygames.enums;

import java.util.Arrays;

public enum YugiohCardAttribute {
    DARK,
    DIVINE,
    EARTH,
    FIRE,
    LIGHT,
    WATER,
    WIND;

    public static boolean isInEnum(String value) {
        return Arrays.stream(YugiohCardAttribute.values()).anyMatch(e -> e.name().equals(value));
    }
}
