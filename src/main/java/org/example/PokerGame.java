package org.example;

public class PokerGame {
    public static void main(String[] args) {
        Dealer dealer = new MyVariationDealer();
        Board board = dealer.dealCardsToPlayers();
        Board board1 = dealer.dealFlop(board);
        Board board2 = dealer.dealTurn(board1);
        Board board3 = dealer.dealRiver(board2);
        PokerResult result = dealer.decideWinner(board3);
        System.out.println(board3.toString());
        System.out.println(result);

        System.out.println(dealer.decideWinner(new Board("2C,3C","5H,QS","2C,JH,JS", "6H", "7C")));



    }
}