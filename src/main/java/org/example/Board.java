package org.example;

/**
 * Покерный стол
 */
public class Board {
    /**
     * Две карты первого игрока
     */
    private final String playerOne;
    /**
     * Две карты второго игрока
     */
    private final String playerTwo;
    /**
     * Три карты, открываются после раздачи карт игрокам
     */
    private final String flop;
    /**
     * Одна карта, открывается после flop
     */
    private final String turn;
    /**
     * Одна карта, открывается после turn
     */
    private final String river;

    public String getPlayerOne() {
        return playerOne;
    }

    public String getPlayerTwo() {
        return playerTwo;
    }

    public String getFlop() {
        return flop;
    }

    public String getTurn() {
        return turn;
    }

    public String getRiver() {
        return river;
    }

    public Board(String playerOne, String playerTwo, String flop, String turn, String river) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.flop = flop;
        this.turn = turn;
        this.river = river;
    }

    @Override
    public String toString() {
        return "Board{" +
                "playerOne='" + playerOne + '\'' +
                ", playerTwo='" + playerTwo + '\'' +
                ", flop='" + flop + '\'' +
                ", turn='" + turn + '\'' +
                ", river='" + river + '\'' +
                '}';
    }
}
