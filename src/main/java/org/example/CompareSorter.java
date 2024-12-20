package org.example;

import java.util.*;

public class CompareSorter {
    RankSuitGet rankSuitGet = new RankSuitGet();
    public String[] sorter(List<String> cards1, int amount) {
        String[] order = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        Map<String, Integer> rankCount1 = new HashMap<>(rankSuitGet.getRankCount(cards1));
        List<String> setKeys1 = new ArrayList<>();
        for (Map.Entry<String, Integer> rank1 : rankCount1.entrySet()) {
            if (rank1.getValue() == amount) {
                setKeys1.add(rank1.getKey());
            }
        }
        Collections.sort(setKeys1, Comparator.comparingInt(rank -> Arrays.asList(order).indexOf(rank)));
        Collections.reverse(setKeys1);
        String[] list1a = setKeys1.toArray(new String[0]);
        return list1a;
    }
}
