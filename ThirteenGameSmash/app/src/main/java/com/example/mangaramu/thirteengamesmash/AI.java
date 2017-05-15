package com.example.mangaramu.thirteengamesmash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AI implements Player {
    private ThirteenGameLogic gl = new ThirteenGameLogic();
    private ArrayList<Integer> cardsSelected = new ArrayList<Integer>();
    private ArrayList<Integer> playableCombinations = new ArrayList<Integer>();

    public ArrayList<Integer> takeTurn(ArrayList<Integer> playPile, ArrayList<Integer> hand) {
        Collections.sort(hand);
        Collections.sort(playPile);
        cardsSelected.clear();

        if (playPile.size() == 0) {
            if (hand.contains(0)) {
                cardsSelected.add(0);
                return cardsSelected;
            } else {
                findPossibleCombinations(playPile, hand);
                Collections.shuffle(playableCombinations);
                int combo = playableCombinations.get(0);

                switch (combo) {
                    case 0:
                        hasSingle(playPile, hand);
                        break;
                    case 1:
                        hasDouble(playPile, hand);
                        break;
                    case 2:
                        hasTriple(playPile, hand);
                        break;
                    case 3:
                        hasStraight(playPile, hand);
                        break;
                    case 4:
                        hasFourOfAKind(playPile, hand);
                        break;
                    case 5:
                        hasBomb(playPile, hand);
                        break;
                }

                playableCombinations.clear();
                return cardsSelected;
            }


        } else {
            if (gl.isSingle(playPile)) {
                if (hasSingle(playPile, hand)) {
                    return cardsSelected;
                }
            }
            if (gl.isDouble(playPile)) {
                if (hasDouble(playPile, hand)) {
                    return cardsSelected;
                }
            }
            if (gl.isTriple(playPile)) {
                if (hasTriple(playPile, hand)) {
                    return cardsSelected;
                }
            }
            if (gl.isTwos(playPile) && (playPile.size() < 4)) {
                if (hasFourOfAKind(playPile, hand)) {
                    return cardsSelected;
                }
                if (hasBomb(playPile, hand)) {
                    return cardsSelected;
                }
            }
            if (gl.isStraight(playPile)) {
                if (hasStraight(playPile, hand)) {
                    return cardsSelected;
                }
            }
            if (gl.isFourOfAKind(playPile)) {
                if (hasFourOfAKind(playPile, hand)) {
                    return cardsSelected;
                }
            }
            if (gl.isBomb(playPile)) {
                if (hasBomb(playPile, hand)) {
                    return cardsSelected;
                }
            }
        }

        cardsSelected.clear();
        return cardsSelected;
    }

    private boolean hasSingle(ArrayList<Integer> playPile, ArrayList<Integer> hand) {
        for (Integer value : hand) {
            cardsSelected.add(value);
            if (gl.isSingle(cardsSelected) && gl.checkCardsSelected(playPile, cardsSelected)) {
                return true;
            }
            cardsSelected.clear();
        }
        return false;
    }

    private boolean hasDouble(ArrayList<Integer> playPile, ArrayList<Integer> hand) {
        for (int i = 0; i < hand.size() - 1; i++) {
            cardsSelected.add(hand.get(i));
            cardsSelected.add(hand.get(i + 1));
            if (gl.isDouble(cardsSelected) && gl.checkCardsSelected(playPile, cardsSelected)) {
                return true;
            }
            cardsSelected.clear();
        }
        return false;
    }

    private boolean hasTriple(ArrayList<Integer> playPile, ArrayList<Integer> hand) {
        for (int i = 0; i < hand.size() - 2; i++) {
            cardsSelected.add(hand.get(i));
            cardsSelected.add(hand.get(i + 1));
            cardsSelected.add(hand.get(i + 2));
            if (gl.isTriple(cardsSelected) && gl.checkCardsSelected(playPile, cardsSelected)) {
                return true;
            }
            this.cardsSelected.clear();
        }
        return false;
    }

    private boolean hasStraight(ArrayList<Integer> playPile, ArrayList<Integer> hand) {
        Map<Integer, ArrayList<Integer>> map = new HashMap<Integer, ArrayList<Integer>>();
        ArrayList<Integer> numbers = new ArrayList<Integer>();

        for (Integer num : hand) {
            if (!numbers.contains(num / 4)) {
                numbers.add(num / 4);
                map.put(num / 4, new ArrayList<Integer>());
            }
            map.get(num / 4).add(num);
        }

        Collections.sort(numbers);
        int sizeOfMinimumStraight;
        boolean empty = false;
        if (playPile.size() > 0) {
            sizeOfMinimumStraight = playPile.size();
        } else {
            sizeOfMinimumStraight = 3;
            empty = true;
        }
        while (sizeOfMinimumStraight <= numbers.size()) {
            for (int i = 0; i < numbers.size() - sizeOfMinimumStraight + 1; i++) {
                for (int j = 0; j < sizeOfMinimumStraight - 1; j++) {
                    cardsSelected.add(map.get(numbers.get(i + j)).get(0));
                }
                for (int k = 0; k < map.get(numbers.get(i + sizeOfMinimumStraight - 1)).size(); k++) {
                    cardsSelected.add(map.get(numbers.get(i + sizeOfMinimumStraight - 1)).get(k));
                    if (gl.checkCardsSelected(playPile, cardsSelected)) {
                        return true;
                    }
                    cardsSelected.remove(map.get(numbers.get(i + sizeOfMinimumStraight - 1)).get(k));
                }
                this.cardsSelected.clear();
            }
            if (empty) {
                sizeOfMinimumStraight++;
            } else {
                break;
            }
        }
        return false;
    }

    private boolean hasFourOfAKind(ArrayList<Integer> playPile, ArrayList<Integer> hand) {
        for (int i = 0; i < hand.size() - 3; i++) {
            cardsSelected.add(hand.get(i));
            cardsSelected.add(hand.get(i + 1));
            cardsSelected.add(hand.get(i + 2));
            cardsSelected.add(hand.get(i + 3));
            if (gl.checkCardsSelected(playPile, cardsSelected)) {
                return true;
            }
            this.cardsSelected.clear();
        }
        return false;
    }

    private boolean hasBomb(ArrayList<Integer> playPile, ArrayList<Integer> hand) {
        Map<Integer, ArrayList<Integer>> map = new HashMap<Integer, ArrayList<Integer>>();
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        int numberOfNumbersNeeded;
        boolean consecutive, pairs;
        int currentNumber;
        int lastNumber, numberOfCardsOfLastNumber;

        for (Integer card : hand) {
            int number = card / 4;
            if (!numbers.contains(number)) {
                numbers.add(number);
                map.put(number, new ArrayList<Integer>());
            }
            map.get(number).add(card);
        }

        boolean empty = false;
        if (gl.isTwos(playPile)) {
            numberOfNumbersNeeded = playPile.size() + 2;
        } else if (gl.isBomb(playPile)) {
            numberOfNumbersNeeded = playPile.size() / 2;
        } else {
            numberOfNumbersNeeded = 3;
            empty = true;
        }
        Collections.sort(numbers);

        while (numberOfNumbersNeeded <= numbers.size()) {
            for (int i = 0; i < (numbers.size() - numberOfNumbersNeeded + 1); i++) {
                consecutive = true;
                pairs = true;

                for (int j = 0; j < numberOfNumbersNeeded - 1; j++) {
                    // Check if the next number in the straight is present
                    currentNumber = numbers.get(i + j);
                    if (numbers.contains(currentNumber + 1)) {
                        if (map.get(currentNumber).size() >= 2) {
                            cardsSelected.add(map.get(currentNumber).get(0));
                            cardsSelected.add(map.get(currentNumber).get(1));
                        } else {
                            pairs = false;
                            break;
                        }
                    } else {
                        consecutive = false;
                        break;
                    }
                }

                lastNumber = numbers.get(i + numberOfNumbersNeeded - 1);
                numberOfCardsOfLastNumber = map.get(lastNumber).size();

                if (consecutive && pairs && (numberOfCardsOfLastNumber > 1)) {
                    cardsSelected.add(map.get(lastNumber).get(0));
                    for (int j = 1; j < numberOfCardsOfLastNumber; j++) {
                        cardsSelected.add(map.get(lastNumber).get(j));
                        if (gl.checkCardsSelected(playPile, cardsSelected)) {
                            return true;
                        }
                        cardsSelected.remove(map.get(lastNumber).get(0));
                    }
                }
                this.cardsSelected.clear();
            }
            if (empty) {
                numberOfNumbersNeeded++;
            } else {
                break;
            }
        }
        return false;
    }

    private void findPossibleCombinations(ArrayList<Integer> playPile, ArrayList<Integer> hand) {
        if (hasSingle(playPile, hand)) {
            playableCombinations.add(0);
        }
        if (hasDouble(playPile, hand)) {
            playableCombinations.add(1);
        }
        if (hasTriple(playPile, hand)) {
            playableCombinations.add(2);
        }
        if (hasStraight(playPile, hand)) {
            playableCombinations.add(3);
        }
        if (hasFourOfAKind(playPile, hand)) {
            playableCombinations.add(4);
        }
        if (hasBomb(playPile, hand)) {
            playableCombinations.add(5);
        }
    }

    @Override
    public Boolean isHosthuman() {
        return false;
    }

    @Override
    public Boolean isClienthuman() {
        return false;
    }

    @Override
    public Boolean isAI() {
        return true;
    }
}
