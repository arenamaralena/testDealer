package org.example;

import java.util.*;

public class MyVariationDealer implements Dealer {
    List<String> cardDeck = new ArrayList<>();
    String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    String[] suits = {"C", "D", "H", "S"};
    Checks checks = new Checks();

    private static List<String> parseCards(String hand) {
        List<String> cards = new ArrayList<>();//для анализа карт
        String regex = "(10|[2-9]|[JQKA])[CDHS]";//регулярное значение
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex); //создание объекта из регулярного значения
        java.util.regex.Matcher matcher = pattern.matcher(hand);//объект проверяющий совпаления с паттерном

        while (matcher.find()) { //ищет следующее совпадение в строке
            cards.add(matcher.group()); //возвращает найденое совпадение и добавляет в лист
        }
        return cards;
    }

    @Override
    public Board dealCardsToPlayers() {

        for (String rank : ranks) { //собираем колоду
            for (String suit : suits) {
                cardDeck.add(rank + suit);
            }
        }
        Collections.shuffle(cardDeck);
        String handOne = (String.join("", (cardDeck.subList(0, 2)))); // выдаем карты
        String handTwo = (String.join("", (cardDeck.subList(2, 4))));
        return new Board(handOne, handTwo, null, null, null);
    }

    @Override
    public Board dealFlop(Board board) {
        if (board == null) throw new InvalidPokerBoardException("нет карт!");
        String flop = String.join("", (cardDeck.subList(4, 7)));
        return new Board(board.getPlayerOne(), board.getPlayerTwo(), flop, null, null);
    }

    @Override
    public Board dealTurn(Board board) {
        if (board == null) throw new InvalidPokerBoardException("нет карт!");
        String turn = String.join("", (cardDeck.subList(7, 8)));
        return new Board(board.getPlayerOne(), board.getPlayerTwo(), board.getFlop(), turn, null);
    }

    @Override
    public Board dealRiver(Board board) {
        if (board == null) throw new InvalidPokerBoardException("нет карт!");
        String river = String.join("", (cardDeck.subList(8, 9)));
        return new Board(board.getPlayerOne(), board.getPlayerTwo(), board.getFlop(), board.getTurn(), river);
    }

    @Override
    public PokerResult decideWinner(Board board) {

        if (board == null) throw new InvalidPokerBoardException("нет карт!");
        if (board.getPlayerOne() == null) throw new InvalidPokerBoardException("нет карт!");
        if (board.getPlayerTwo() == null) throw new InvalidPokerBoardException("нет карт!");
        if (board.getFlop() == null) throw new InvalidPokerBoardException("нет карт!");
        if (board.getTurn() == null) throw new InvalidPokerBoardException("нет карт!");
        if (board.getRiver() == null) throw new InvalidPokerBoardException("нет карт!");


        List<String> flopB = new ArrayList<>(parseCards(board.getFlop()));
        List<String> firstCards = new ArrayList<>(parseCards(board.getPlayerOne())); //привести стринг руки к листу
        List<String> secondCards = new ArrayList<>(parseCards(board.getPlayerTwo()));

        List<String> cardsOnTable = new ArrayList<>(); //создаем стол
        cardsOnTable.addAll(flopB);
        cardsOnTable.add(board.getTurn());
        cardsOnTable.add(board.getRiver());

        List<String> cardsOnTableWH = new ArrayList<>(); //создаем стол вместе с руками для проверки
        cardsOnTableWH.addAll(cardsOnTable);
        cardsOnTableWH.addAll(firstCards);
        cardsOnTableWH.addAll(secondCards);

        List<String> duplicates = new ArrayList<>();//множество для отслеживания повторяющихся элементов
        Set<String> tracking = new HashSet<>();
        for (String card : cardsOnTableWH) {//добавляем их во множество для отслеживания
            //если не получилось добавить, то значит уже встречался в списке
            if (!tracking.add(card)) {
                //в этом случае добавляем его во множество дубликатов
                duplicates.add(card);
            }
        }
        if (!duplicates.isEmpty()) throw new InvalidPokerBoardException("повтор карт!");

        firstCards.addAll(cardsOnTable);
        secondCards.addAll(cardsOnTable); // объединение со столом для анализа
        int firstHand = checks.analisCards(firstCards);
        int secondHand = checks.analisCards(secondCards);
        PokerResult highCardDec = checks.checkKiker(firstCards, secondCards, 5);

        if (firstHand > secondHand) return PokerResult.PLAYER_ONE_WIN;
        else if (secondHand > firstHand)
            return PokerResult.PLAYER_TWO_WIN;
        else if (firstHand == 9 & secondHand == 9) {
            return checks.straightCompare(firstCards, secondCards);
        } else if (firstHand == 8 & secondHand == 8) {
            return checks.compareFour(firstCards, secondCards, 1);
        } else if (firstHand == 7 & secondHand == 7) {
            if (checks.compareSet(firstCards, secondCards, 1) == PokerResult.DRAW) {
                return checks.comparePair(firstCards, secondCards, 1);
            }
            return checks.compareSet(firstCards, secondCards, 1);
        } else if (firstHand == 6 & secondHand == 6) {
            return checks.flushCompare(firstCards, secondCards);
        } else if (firstHand == 5 & secondHand == 5) {
            return checks.straightCompare(firstCards, secondCards);
        } else if (firstHand == 4 & secondHand == 4) {
            return checks.compareSet(firstCards, secondCards, 2);
        } else if (firstHand == 3 & secondHand == 3) {
            return checks.comparePair(firstCards, secondCards, 1);
        } else if (firstHand == 2 & secondHand == 2) {
            return checks.comparePair(firstCards, secondCards, 3);
        } else
            return highCardDec;
    }
}



