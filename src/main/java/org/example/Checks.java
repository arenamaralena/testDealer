package org.example;

import java.util.*;

public class Checks {
    CompareSorter compare = new CompareSorter();
    RankSuitGet rankSuitGet = new RankSuitGet();
    public static String[] getRankValues() {
        Ranks[] ranks = Ranks.values();
        String[] values = new String[ranks.length];

        for (int i = 0; i < ranks.length; i++) {
            values[i] = ranks[i].getValue();
        }

        return values;
    }
    String[] order = getRankValues();

    public int analisCards(List<String> cards) {
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
        HashMap<String, Integer> countSuits = new HashMap<>(rankSuitGet.getCountSuits(cards));
        for (int count : countSuits.values()) {
            if (count >= 5) {
                return true; // Флеш найден
            }
        }
        return false; // Флеш не найден
    }

    public boolean checkPair(List<String> cards) {
        Map<String, Integer> rankCount = new HashMap<>(rankSuitGet.getRankCount(cards));
        // Проверяем, есть ли пара
        for (int count : rankCount.values()) {
            if (count == 2) {
                return true; // Пара найдена
            }
        }

        return false;
    }

    public boolean checkSet(List<String> cards) {
        Map<String, Integer> rankCount = new HashMap<>(rankSuitGet.getRankCount(cards));
        for (int count : rankCount.values()) {
            if (count == 3) {
                return true; // найден сет
            }
        }
        return false;
    }

    public boolean check2Pair(List<String> cards) {
        Map<String, Integer> rankCount = new HashMap<>(rankSuitGet.getRankCount(cards));
        // Проверяем, есть ли пары
        int pair = 0;
        for (int count : rankCount.values()) {
            if (count == 2) {
                pair++;
            }
        }
        return pair >= 2;
    }

    public boolean checkFour(List<String> cards) {
        Map<String, Integer> rankCount = new HashMap<>(rankSuitGet.getRankCount(cards));
        for (int count : rankCount.values()) {
            if (count == 4) {
                return true; // Каре!
            }
        }
        return false;
    }

    public boolean checkStraight(List<String> cards) {


        List<String> sortedRanks = new ArrayList<>(sortingRank(cards));
        Collections.reverse(sortedRanks);//делаем из сета лист
        int consecutiveCount = 1;
        for (int i = 1; i < sortedRanks.size(); i++) {
            if (Arrays.asList(order).indexOf(sortedRanks.get(i)) - Arrays.asList(order).indexOf(sortedRanks.get(i - 1)) == 1) {
                consecutiveCount++;
                if (consecutiveCount >= 5) {
                    return true; // Стрит найден
                }
            } else if (Arrays.asList(order).indexOf(sortedRanks.get(i)) - Arrays.asList(order).indexOf(sortedRanks.get(i - 1)) > 1) {
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

    public PokerResult checkKiker(List<String> cards1, List<String> cards2, int g) {
        String[] list1 = sortingRank(cards1).toArray(new String[0]);
        String[] list2 = sortingRank(cards2).toArray(new String[0]);
        for (int i = 0; i < Math.min(g, (Math.min(list1.length, list2.length))); i++) {
            int comparison = Integer.compare(indexOf(order, list1[i]), indexOf(order, list2[i]));
            if (comparison > 0) {
                return PokerResult.PLAYER_ONE_WIN; // Игрок 1 выигрывает
            } else if (comparison < 0) {
                return PokerResult.PLAYER_TWO_WIN; // Игрок 2 выигрывает
            }
        }
        // Если все карты равны или все карты были проверены и равны
        return PokerResult.DRAW;


    }

    public PokerResult compareSet(List<String> cards1, List<String> cards2, int g) {
        String[] list1 = compare.sorter(cards1, 3);
        String[] list2 = compare.sorter(cards2, 3);
        if (chooseWinner(list1, list2) == PokerResult.DRAW) {
            // Если все карты равны или все карты были проверены и равны
            return checkKiker(cards1, cards2, g);
        }
        return chooseWinner(list1, list2);
    }

    public PokerResult comparePair(List<String> cards1, List<String> cards2, int g) {
        String[] list1 = compare.sorter(cards1, 2);
        String[] list2 = compare.sorter(cards2, 2);
        if (chooseWinner(list1, list2) == PokerResult.DRAW) {
            // Если все карты равны или все карты были проверены и равны
            return checkKiker(cards1, cards2, g);
        }
        return chooseWinner(list1, list2);
    }

    public PokerResult compareFour(List<String> cards1, List<String> cards2, int g) {

        String[] list1 = compare.sorter(cards1, 4);
        String[] list2 = compare.sorter(cards2, 4);
        if (chooseWinner(list1, list2) == PokerResult.DRAW) {
            // Если все карты равны или все карты были проверены и равны
            return checkKiker(cards1, cards2, g);
        }
        return chooseWinner(list1, list2);

    }

    public PokerResult straightCompare(List<String> card1, List<String> card2) {
        List<String> sortedRanks1 = new ArrayList<>(sortingRank(card1));
        List<String> highCard1 = new ArrayList<>();
        for (int i = 1; i < sortedRanks1.size(); i++) {
            if (Arrays.asList(order).indexOf(sortedRanks1.get(i - 1)) - Arrays.asList(order).indexOf(sortedRanks1.get(i)) == 1) {
                highCard1.add(sortedRanks1.get(i - 1));
            } else if (Arrays.asList(order).indexOf(sortedRanks1.get(i - 1)) - Arrays.asList(order).indexOf(sortedRanks1.get(i)) > 1) {
                highCard1.remove(0);// Сброс счётчика, если разрыв
            }
        }
        List<String> sortedRanks2 = new ArrayList<>(sortingRank(card2));
        List<String> highCard2 = new ArrayList<>();
        for (int i = 1; i < sortedRanks2.size(); i++) {
            if ((Arrays.asList(order).indexOf(sortedRanks2.get(i - 1)) - Arrays.asList(order).indexOf(sortedRanks2.get(i)) == 1)) {
                highCard2.add(sortedRanks2.get(i - 1));
            } else if (Arrays.asList(order).indexOf(sortedRanks2.get(i - 1)) - Arrays.asList(order).indexOf(sortedRanks2.get(i)) > 1) {
                highCard2.remove(0);// Сброс счётчика, если разрыв
            }
        }
        if (Arrays.asList(order).indexOf(highCard1.get(0)) > Arrays.asList(order).indexOf(highCard2.get(0))) {
            return PokerResult.PLAYER_ONE_WIN;
        } else if (Arrays.asList(order).indexOf(highCard1.get(0)) < Arrays.asList(order).indexOf(highCard2.get(0))) {
            return PokerResult.PLAYER_TWO_WIN;
        } else return PokerResult.DRAW;
    }

    public PokerResult flushCompare(List<String> card1, List<String> card2) {
        HashMap<String, Integer> countSuits1 = new HashMap<>(rankSuitGet.getCountSuits(card1));
        String firstSuit = null;
        for (Map.Entry<String, Integer> suit1 : countSuits1.entrySet()) {
            if (suit1.getValue() == 5) {
                firstSuit = suit1.getKey();
            }
        }
        Set<String> uniqueRanks1 = new HashSet<>();
        for (String card : card1) {
            if (card.substring(card.length() - 1).equals(firstSuit)) {
                uniqueRanks1.add(card.substring(0, card.length() - 1));
            } // Получаем ранг карты
        }
        List<String> sortedRanks1 = new ArrayList<>(uniqueRanks1);//делаем из сета лист
        Collections.sort(sortedRanks1, Comparator.comparingInt(rank -> Arrays.asList(order).indexOf(rank)));
        Collections.reverse(sortedRanks1);

        HashMap<String, Integer> countSuits2 = new HashMap<>(rankSuitGet.getCountSuits(card1));
        String secondSuit = null;
        for (Map.Entry<String, Integer> suit2 : countSuits2.entrySet()) {
            if (suit2.getValue() == 5) {
                secondSuit = suit2.getKey();
            }
        }
        Set<String> uniqueRanks2 = new HashSet<>();
        for (String card : card2) {
            if (card.substring(card.length() - 1).equals(secondSuit)) {
                uniqueRanks2.add(card.substring(0, card.length() - 1));
            } // Получаем ранг карты
        }
        List<String> sortedRanks2 = new ArrayList<>(uniqueRanks2);//делаем из сета лист
        Collections.sort(sortedRanks2, Comparator.comparingInt(rank -> Arrays.asList(order).indexOf(rank)));
        Collections.reverse(sortedRanks2);
        String[] list1 = sortedRanks1.toArray(new String[0]);
        String[] list2 = sortedRanks2.toArray(new String[0]);
        return chooseWinner(list1, list2);
    }

    private List<String> sortingRank(List<String> cards1) {
        Set<String> uniqueRanks1 = new HashSet<>();
        for (String card : cards1) {
            uniqueRanks1.add(card.substring(0, card.length() - 1)); // Получаем ранг карты
        }
        List<String> sortedRanks1 = new ArrayList<>(uniqueRanks1);//делаем из сета лист
        Collections.sort(sortedRanks1, Comparator.comparingInt(rank -> Arrays.asList(order).indexOf(rank)));
        Collections.reverse(sortedRanks1);
        return sortedRanks1;
    }

    private int indexOf(String[] order, String mean) {
        for (int i = 0; i < order.length; i++) {
            if (order[i].equals(mean)) {
                return i;
            }
        }
        return -1; // Если ранг не найден, возвращаем -1
    }

    private PokerResult chooseWinner(String[] list1, String[] list2) {
        for (int i = 0; i < Math.min(list1.length, list2.length); i++) {
            int comparison = Integer.compare(indexOf(order, list1[i]), indexOf(order, list2[i]));
            if (comparison > 0) {
                return PokerResult.PLAYER_ONE_WIN; // Игрок 1 выигрывает
            } else if (comparison < 0) {
                return PokerResult.PLAYER_TWO_WIN; // Игрок 2 выигрывает
            }
        }
        return PokerResult.DRAW;
    }
}

