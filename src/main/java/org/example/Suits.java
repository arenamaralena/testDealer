package org.example;

public enum Suits {
    CLUBS("C"),
    DIAMONDS("D"),
    HEARTS("H"),
    SPADES("S");

    private final String symbol;

    Suits(String symbol) {
        this.symbol = symbol;
    }

    public String getSuit() {
        return symbol;
    }
}
