package com.example.mangaramu.thirteengamesmash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ThirteenGameLogic {
    private boolean startOfGame = false;

    public ThirteenGameLogic() {
    }

    public ThirteenGameLogic(boolean startOfGame) {
        this.startOfGame = startOfGame;
    }

    public boolean checkCardsSelected(ArrayList<Integer> playPile, ArrayList<Integer> cardsSelected) { // playPile is what has already been played, cardsSelected is what a person selected
        if (playPile.size() == 0) {
            if (startOfGame && !cardsSelected.contains(0)) {
                return false;
            }
            if (isSingle(cardsSelected)) {
                return true;
            } else if (isDouble(cardsSelected)) {
                return true;
            } else if (isTriple(cardsSelected)) {
                return true;
            } else if (isStraight(cardsSelected)) {
                return true;
            } else if (isFourOfAKind(cardsSelected)) {
                return true;
            } else if (isBomb(cardsSelected)) {
                return true;
            } else {
                return false;
            }
        } else {
            if (isSingle(playPile) && isSingle(cardsSelected)) {
                return hasHigherValue(playPile, cardsSelected);
            } else if (isDouble(playPile) && isDouble(cardsSelected)) {
                return hasHigherValue(playPile, cardsSelected);
            } else if (isTriple(playPile) && isTriple(cardsSelected)) {
                return hasHigherValue(playPile, cardsSelected);
            } else if (isTwos(playPile) && (playPile.size() < 4)) {
                if (isBomb(cardsSelected)) {
                    return (playPile.size() <= (cardsSelected.size() / 2 - 2));
                } else
                    return ((playPile.size() == 1) && isFourOfAKind(cardsSelected));
            } else if (isStraight(playPile) && isStraight(cardsSelected)) {
                return hasHigherValue(playPile, cardsSelected);
            } else if (isBomb(playPile) && isBomb(cardsSelected)) {
                return hasHigherValue(playPile, cardsSelected);
            } else if (isFourOfAKind(playPile) && isFourOfAKind(cardsSelected)) {
                return hasHigherValue(playPile, cardsSelected);
            } else if (isDragon(cardsSelected) || (isFourOfAKind(cardsSelected) && isTwos(cardsSelected))) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean isSingle(ArrayList<Integer> cards) {
        if (cards.size() == 1) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isDouble(ArrayList<Integer> cards) {
        if (cards.size() == 2) {
            if ((cards.get(0) / 4) == (cards.get(1) / 4)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isTriple(ArrayList<Integer> cards) {
        if (cards.size() == 3) {
            if (((cards.get(0) / 4) == (cards.get(1) / 4)) && ((cards.get(0) / 4) == (cards.get(2) / 4))) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isStraight(ArrayList<Integer> cards) {
        if (cards.size() >= 3) {
            // Grab all card values
            ArrayList<Integer> values = new ArrayList<Integer>();
            for (Integer number : cards) {
                values.add(number / 4);
            }
            int firstValue = Collections.min(values);

            // If straight ends in 2, return false
            if (firstValue + cards.size() - 1 == 12) {
                return false;
            } else {
                for (int i = 0; i < values.size(); i++) {
                    if (!values.contains(firstValue + i)) {
                        return false;
                    }
                }
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean isBomb(ArrayList<Integer> cards) {
        // Check if number of cards is less than 6 or is odd
        if ((cards.size() < 6) || (cards.size() % 2 == 1)) {
            return false;
        }

        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        int number;
        int firstValue;

        // Grab all the numbers and their occurrences
        for (Integer num : cards) {
            number = num / 4;
            if (!map.containsKey(number)) {
                map.put(number, 1);
            } else {
                map.put(num / 4, map.get(number) + 1);
            }
        }

        // Check if all pairs
        for (Integer val : map.values()) {
            if (val != 2) {
                return false;
            }
        }

        // Check if numbers are consecutive
        firstValue = Collections.min(map.keySet());
        for (int i = 0; i < map.size(); i++) {
            if (!map.containsKey(firstValue + i)) {
                return false;
            }
        }
        return true;
    }

    public boolean isFourOfAKind(ArrayList<Integer> cards) {
        if (cards.size() == 4) {
            int number = cards.get(0) / 4;
            if ((number == (cards.get(1) / 4)) &&
                    (number == (cards.get(2) / 4)) &&
                    (number == (cards.get(3) / 4))) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isDragon(ArrayList<Integer> cards) {
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        for (Integer num : cards) {
            if (!numbers.contains(num / 4)) {
                numbers.add(num / 4);
            }
        }

        if (numbers.size() == 13) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isTwos(ArrayList<Integer> cards) {
        if ((cards.size() >= 1) && (cards.size() <= 4)) {
            for (Integer num : cards) {
                if (num < 48) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean hasHigherValue(ArrayList<Integer> playedCards, ArrayList<Integer> cardsSelected) {
        if (cardsSelected.size() == playedCards.size()) {
            if (Collections.max(cardsSelected) > Collections.max(playedCards)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void updateStartOfGame(boolean startOfGame) {
        this.startOfGame = startOfGame;
    }

    // Returns a map with integers and their respective card's resource id
    public HashMap<Integer, Integer> generateCardMapping() {
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

        map.put(0, R.drawable.s_3);
        map.put(1, R.drawable.c_3);
        map.put(2, R.drawable.d_3);
        map.put(3, R.drawable.h_3);
        map.put(4, R.drawable.s_4);
        map.put(5, R.drawable.c_4);
        map.put(6, R.drawable.d_4);
        map.put(7, R.drawable.h_4);
        map.put(8, R.drawable.s_5);
        map.put(9, R.drawable.c_5);
        map.put(10, R.drawable.d_5);
        map.put(11, R.drawable.h_5);
        map.put(12, R.drawable.s_6);
        map.put(13, R.drawable.c_6);
        map.put(14, R.drawable.d_6);
        map.put(15, R.drawable.h_6);
        map.put(16, R.drawable.s_7);
        map.put(17, R.drawable.c_7);
        map.put(18, R.drawable.d_7);
        map.put(19, R.drawable.h_7);
        map.put(20, R.drawable.s_8);
        map.put(21, R.drawable.c_8);
        map.put(22, R.drawable.d_8);
        map.put(23, R.drawable.h_8);
        map.put(24, R.drawable.s_9);
        map.put(25, R.drawable.c_9);
        map.put(26, R.drawable.d_9);
        map.put(27, R.drawable.h_9);
        map.put(28, R.drawable.s_10);
        map.put(29, R.drawable.c_10);
        map.put(30, R.drawable.d_10);
        map.put(31, R.drawable.h_10);
        map.put(32, R.drawable.s_11);
        map.put(33, R.drawable.c_11);
        map.put(34, R.drawable.d_11);
        map.put(35, R.drawable.h_11);
        map.put(36, R.drawable.s_12);
        map.put(37, R.drawable.c_12);
        map.put(38, R.drawable.d_12);
        map.put(39, R.drawable.h_12);
        map.put(40, R.drawable.s_13);
        map.put(41, R.drawable.c_13);
        map.put(42, R.drawable.d_13);
        map.put(43, R.drawable.h_13);
        map.put(44, R.drawable.s_1);
        map.put(45, R.drawable.c_1);
        map.put(46, R.drawable.d_1);
        map.put(47, R.drawable.h_1);
        map.put(48, R.drawable.s_2);
        map.put(49, R.drawable.c_2);
        map.put(50, R.drawable.d_2);
        map.put(51, R.drawable.h_2);

        return map;
    }

}

