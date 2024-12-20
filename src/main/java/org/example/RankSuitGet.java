package org.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankSuitGet {
    public Map<String, Integer> getCountSuits(List<String> cards) {
        Map<String, Integer> countSuits = new HashMap<>();
        for (String card : cards) {
            String suit = card.substring(card.length() - 1); // Получаем масть карты
            countSuits.put(suit, countSuits.getOrDefault(suit, 0) + 1); // Увеличиваем счетчик для данной масти
        }
        return countSuits;
    }
    public Map<String, Integer> getRankCount(List<String> cards) {
        Map<String, Integer> rankCount = new HashMap<>();

        for (String card : cards) {
            String rank = card.substring(0, card.length() - 1); // Получаем ранг карты
            rankCount.put(rank, rankCount.getOrDefault(rank, 0) + 1); // Увеличиваем счетчик для данного ранга
        }
        return rankCount;
    }

}
