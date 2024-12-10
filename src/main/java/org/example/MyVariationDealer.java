package org.example;

import java.util.*;

public class MyVariationDealer implements Dealer {
    List<String> cardDeck = new ArrayList<>();
    String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    String[] suits = {"C", "D", "H", "S"};
    @Override
    public Board dealCardsToPlayers() {

        for (String rank : ranks) { //собираем колоду
            for (String suit : suits) {
                cardDeck.add(rank + suit);
            }
        }
        Collections.shuffle(cardDeck);
        String handOne = removeBrackets(String.valueOf(cardDeck.subList(0, 2))); // выдаем карты
        String handTwo = removeBrackets(String.valueOf(cardDeck.subList(2, 4)));
        return new Board(handOne,handTwo, null, null, null);
    }

    @Override
    public Board dealFlop(Board board) {
        String flop = removeBrackets(String.valueOf(cardDeck.subList(4,7)));
        return new Board(board.getPlayerOne(), board.getPlayerTwo(), flop, null, null);
    }

    @Override
    public Board dealTurn(Board board) {
        String turn = removeBrackets(String.valueOf(cardDeck.subList(7,8)));
        return new Board(board.getPlayerOne(), board.getPlayerTwo(), board.getFlop(), turn, null);
    }

    @Override
    public Board dealRiver(Board board) {
        String river = removeBrackets(String.valueOf(cardDeck.subList(8,9)));
        return new Board(board.getPlayerOne(), board.getPlayerTwo(), board.getFlop(), board.getTurn(), river);
    }

    @Override
    public PokerResult decideWinner(Board board) {

        String[] tableGame = {board.getFlop(), board.getRiver(), board.getTurn()}; // объединение игры на столе
        ArrayList<String> table = new ArrayList<>(Arrays.asList(tableGame));
        for (int i = 0; i < table.size(); i++) {
            String[] splitElements = table.get(i).split(",\\s*");
            table.remove(i); // Удаляем старый элемент
            table.addAll(i, Arrays.asList(splitElements)); // Добавляем разделенные элементы
        }//убираем скобки
        List<String> firstCards = new ArrayList<>(Arrays.asList(board.getPlayerOne().split(",\\s*"))); //привести стринг руки к листу
        List<String> secondCards = new ArrayList<>(Arrays.asList(board.getPlayerTwo().split(",\\s*")));
        firstCards.addAll(table);

        List<String> cardsOnTable =  firstCards;
        cardsOnTable.addAll(secondCards);
        List<String> duplicates = new ArrayList<>() ;//множество для отслеживания повторяющихся элементов
        Set<String> tracking = new HashSet<>();
        for (String crad: cardsOnTable) {//добавляем их во множество для отслеживания
            //если не получилось добавить, то значит уже встречался в списке
            if (!tracking.add(crad)) {
                //в этом случае добавляем его во множество дубликатов
                duplicates.add(crad);
            }
        }
        if (!duplicates.isEmpty()) throw new InvalidPokerBoardException("повтор карт!");


         // объединение со столом для анализа
        secondCards.addAll(table);
        int firstHand = analisCards(firstCards);
        int secondHand = analisCards(secondCards);

        if (firstHand > secondHand) return PokerResult.PLAYER_ONE_WIN;
        else if (secondHand > firstHand)
            return PokerResult.PLAYER_TWO_WIN;
        else if ((firstHand == 1) & (secondHand == 1)) { return highCard(firstCards,secondCards);}
        else
            return PokerResult.DRAW;
    }
    private int analisCards(List<String> cards){
        if (royalFlush(cards)) {
            System.out.println("РоялФлеш");
            return 10;
        } else if (checkFlush(cards) && checkStraight(cards)) {
            System.out.println("СтритФлеш");
            return 9;
        } else if (checkFour(cards)) {//каре
            System.out.println("Каре");
           return 8;
        } else if (checkSet(cards)&&checkPair(cards)) {//фулхауз
            System.out.println("Фулхауз");
            return 7;
        } else if (checkFlush(cards)) {
            System.out.println("Флеш");
            return 6;
        } else if (checkStraight(cards)) {
            System.out.println("Стрит");
            return 5;
        } else if (checkSet(cards)) {//сет
            System.out.println("Сет");
            return 4;
        } else if (check2Pair(cards)) {//две пары
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

    public boolean checkFlush (List<String> cards){
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

    public boolean checkPair(List<String> cards){
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

    return false;}

    public boolean checkSet(List<String>cards){
        Map<String, Integer> rankCount = new HashMap<>();
        for (String card : cards) {
            String rank = card.substring(0, card.length() - 1); // Получаем ранг карты
            rankCount.put(rank, rankCount.getOrDefault(rank, 0) + 1); // Увеличиваем счетчик для данного ранга
        }

        // Проверяем, есть ли пара
        for (int count : rankCount.values()) {
            if (count == 3) {
                return true; // найден сет
            }
        }
    return false;}

    public boolean check2Pair(List<String> cards){
        Map<String, Integer> rankCount = new HashMap<>();

        for (String card : cards) {
            String rank = card.substring(0, card.length() - 1); // Получаем ранг карты
            rankCount.put(rank, rankCount.getOrDefault(rank, 0) + 1); // Увеличиваем счетчик для данного ранга
        }
        // Проверяем, есть ли пара
        int pair = 0;
        for (int count : rankCount.values()) {
            if (count == 2) {
                 pair++;
            }
        }
        if (pair == 2){
            return true;
        }
        return false;}

    public boolean checkFour(List<String>cards){
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
        return false;}

    public boolean checkStraight(List<String> cards){
        Set<String> uniqueRanks = new HashSet<>();
        for (String card : cards) {
            uniqueRanks.add(card.substring(0, card.length() - 1)); // Получаем ранг карты
        }

        List<String> sortedRanks = new ArrayList<>(uniqueRanks);
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
        return false;}

    public boolean royalFlush(List<String> cards){
        String[] royalRanks = {"10", "J", "Q", "K", "A"};
        Map<Character, Integer> suitCount = new HashMap<>();

        for (String card : cards) {
            char suit = card.charAt(card.length() - 1);
            suitCount.put(suit, suitCount.getOrDefault(suit, 0) + 1);
        }

        for (Character suit : suitCount.keySet()) {
            int count = 0;
            for (String rank : royalRanks) {
                if (cards.contains(rank + suit)) {
                    count++;
                }
            }
            if (count == 5) {
                return true; // Найден флеш-рояль
            }
        }
    return false;}

    public PokerResult highCard (List<String> cards1,List<String> cards2){
        String[] order = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        String[] list1a = cards1.toArray(new String[0]);
        String[] list2a = cards2.toArray(new String[0]);
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
    private static String removeBrackets(String string) {
        // Убираем первый и последний символы, если они скобки
        if (string.startsWith("[") && string.endsWith("]")) {
            return string.substring(1, string.length() - 1);
        }
        return string; // Возвращаем строку без изменений, если скобок нет
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


