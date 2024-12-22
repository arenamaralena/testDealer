package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Ranks  {
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("10"),
    JACK("J"),
    QUEEN("Q"),
    KING("K"),
    ACE("A");

    private final String value;

    Ranks(String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }
}

