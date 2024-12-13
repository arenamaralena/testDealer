package org.example;

import java.util.*;

public class MyVariationDealer implements Dealer {
    List<String> cardDeck = new ArrayList<>();
    String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    String[] suits = {"C", "D", "H", "S"};

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
        PokerResult highCardDec = highCard(firstCards, secondCards);

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
        for (String crad : cardsOnTableWH) {//добавляем их во множество для отслеживания
            //если не получилось добавить, то значит уже встречался в списке
            if (!tracking.add(crad)) {
                //в этом случае добавляем его во множество дубликатов
                duplicates.add(crad);
            }
        }
        if (!duplicates.isEmpty()) throw new InvalidPokerBoardException("повтор карт!");

        firstCards.addAll(cardsOnTable);
        secondCards.addAll(cardsOnTable); // объединение со столом для анализа
        int firstHand = analisCards(firstCards);
        int secondHand = analisCards(secondCards);

        if (firstHand > secondHand) return PokerResult.PLAYER_ONE_WIN;
        else if (secondHand > firstHand)
            return PokerResult.PLAYER_TWO_WIN;
        else
            return highCardDec;
    }

    private int analisCards(List<String> cards) {
        if (royalFlush(cards)) {
            System.out.println("РоялФлеш");
            return 10;
        } else if (checkFlush(cards) && checkStraight(cards)) {
            System.out.println("СтритФлеш");
            return 9;
        } else if (checkFour(cards)) {//каре
            System.out.println("Каре");
            return 8;
        } else if (checkSet(cards) && checkPair(cards)) {
            System.out.println("Фулхауз");
            return 7;
        } else if (checkFlush(cards)) {
            System.out.println("Флеш");
            return 6;
        } else if (checkStraight(cards)) {
            System.out.println("Стрит");
            return 5;
        } else if (checkSet(cards)) {
            System.out.println("Сет");
            return 4;
        } else if (check2Pair(cards)) {
            System.out.println("ДвеПары");
            return 3;
        } else if (checkPair(cards)) {
            System.out.println("Пара");
            return 2;
        } else {
            System.out.println("Старшая");
            return 1;
        }

    }

    public boolean checkFlush(List<String> cards) {
        Map<String, Integer> countSuits = new HashMap<>();
        for (String card : cards) {
            String suit = card.substring(card.length() - 1); // Получаем масть карты
            countSuits.put(suit, countSuits.getOrDefault(suit, 0) + 1); // Увеличиваем счетчик для данной масти
        }
        // Проверяем, есть ли хотя бы одна масть с 5 или более картами
        for (int count : countSuits.values()) {
            if (count >= 5) {
                return true; // Флеш найден
            }
        }
        return false; // Флеш не найден
    }

    public boolean checkPair(List<String> cards) {
        Map<String, Integer> rankCount = new HashMap<>();

        for (String card : cards) {
            String rank = card.substring(0, card.length() - 1); // Получаем ранг карты
            rankCount.put(rank, rankCount.getOrDefault(rank, 0) + 1); // Увеличиваем счетчик для данного ранга
        }

        // Проверяем, есть ли пара
        for (int count : rankCount.values()) {
            if (count == 2) {
                return true; // Пара найдена
            }
        }

        return false;
    }

    public boolean checkSet(List<String> cards) {
        Map<String, Integer> rankCount = new HashMap<>();
        for (String card : cards) {
            String rank = card.substring(0, card.length() - 1); // Получаем ранг карты
            rankCount.put(rank, rankCount.getOrDefault(rank, 0) + 1); // Увеличиваем счетчик для данного ранга
        }

        for (int count : rankCount.values()) {
            if (count == 3) {
                return true; // найден сет
            }
        }
        return false;
    }

    public boolean check2Pair(List<String> cards) {
        Map<String, Integer> rankCount = new HashMap<>();

        for (String card : cards) {
            String rank = card.substring(0, card.length() - 1); // Получаем ранг карты
            rankCount.put(rank, rankCount.getOrDefault(rank, 0) + 1); // Увеличиваем счетчик для данного ранга
        }
        // Проверяем, есть ли пары
        int pair = 0;
        for (int count : rankCount.values()) {
            if (count == 2) {
                pair++;
            }
        }
        if (pair == 2) {
            return true;
        }
        return false;
    }

    public boolean checkFour(List<String> cards) {
        Map<String, Integer> rankCount = new HashMap<>();
        for (String card : cards) {
            String rank = card.substring(0, card.length() - 1); // Получаем ранг карты
            rankCount.put(rank, rankCount.getOrDefault(rank, 0) + 1); // Увеличиваем счетчик для данного ранга
        }
        for (int count : rankCount.values()) {
            if (count == 4) {
                return true; // Каре!
            }
        }
        return false;
    }

    public boolean checkStraight(List<String> cards) {
        Set<String> uniqueRanks = new HashSet<>();
        for (String card : cards) {
            uniqueRanks.add(card.substring(0, card.length() - 1)); // Получаем ранг карты
        }

        List<String> sortedRanks = new ArrayList<>(uniqueRanks);//делаем из сета лист
        Collections.sort(sortedRanks, Comparator.comparingInt(rank -> Arrays.asList(ranks).indexOf(rank)));

        int consecutiveCount = 1;
        for (int i = 1; i < sortedRanks.size(); i++) {
            if (Arrays.asList(ranks).indexOf(sortedRanks.get(i)) - Arrays.asList(ranks).indexOf(sortedRanks.get(i - 1)) == 1) {
                consecutiveCount++;
                if (consecutiveCount >= 5) {
                    return true; // Стрит найден
                }
            } else if (Arrays.asList(ranks).indexOf(sortedRanks.get(i)) - Arrays.asList(ranks).indexOf(sortedRanks.get(i - 1)) > 1) {
                consecutiveCount = 1; // Сброс счётчика, если разрыв
            }
        }
        return false;
    }

    public boolean royalFlush(List<String> cards) {
        String[] royalRanks = {"10", "J", "Q", "K", "A"};//нужные ранги
        Map<Character, Integer> suitCount = new HashMap<>();

        for (String card : cards) {//проверка мастей на количество кард
            char suit = card.charAt(card.length() - 1);
            suitCount.put(suit, suitCount.getOrDefault(suit, 0) + 1);
        }

        for (Character suit : suitCount.keySet()) {
            int count = 0;
            for (String rank : royalRanks) {//проверка нужных рангов в масти
                if (cards.contains(rank + suit)) {
                    count++;
                }
            }
            if (count == 5) {
                return true; // Найден флеш-рояль
            }
        }
        return false;
    }

    public PokerResult highCard(List<String> cards1, List<String> cards2) {
        String[] order = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        Set<String> rangC1 = new HashSet<>();
        for (String card1 : cards1) {
            rangC1.add(card1.substring(0, card1.length() - 1)); // Получаем ранг карты
        }
        List<String> sortedRanks = new ArrayList<>(rangC1);
        Set<String> rangC2 = new HashSet<>();
        for (String card2 : cards2) {
            rangC2.add(card2.substring(0, card2.length() - 1)); // Получаем ранг карты
        }
        List<String> sortedRanks2 = new ArrayList<>(rangC2);

        String[] list1a = sortedRanks.toArray(new String[0]);
        String[] list2a = sortedRanks2.toArray(new String[0]);
        // Сравнение карт поочередно
        for (int k = 0; k < Math.min(list1a.length, list2a.length); k++) {
            int comparison = Integer.compare(indexOf(order, list1a[k]), indexOf(order, list2a[k]));

            if (comparison > 0) {
                return PokerResult.PLAYER_ONE_WIN; // Игрок 1 выигрывает
            } else if (comparison < 0) {
                return PokerResult.PLAYER_TWO_WIN; // Игрок 2 выигрывает
            }
        }
        // Если все карты равны или все карты были проверены и равны
        return PokerResult.DRAW;

    }

    private int indexOf(String[] order, String mean) {
        for (int i = 0; i < order.length; i++) {
            if (order[i].equals(mean)) {
                return i;
            }
        }
        return -1; // Если ранг не найден, возвращаем -1
    }
}


