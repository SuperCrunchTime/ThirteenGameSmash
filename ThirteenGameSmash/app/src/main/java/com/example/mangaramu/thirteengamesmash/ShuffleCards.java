package com.example.mangaramu.thirteengamesmash;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

/**
 * Created by mangaramu on 11/29/2016.
 */

public class ShuffleCards {
Random rand = new Random();


    public ArrayList<Integer> shuffle(ArrayList<Integer> deck) { /* shuffles a nonzero arraylist of card objects. Takes in an arraylist of cards. Returns a boolean
                                                       telling if the deck was a greater than zero size*/
        if(deck.size()!=0) {
            int size = deck.size();
            int lastcard = deck.size() - 1;
            for (int x = lastcard; x >= 0; x--) {/* travels bottom up exchanging a card position denoted by x
                                                    with a card denoted by the random number. */
                int randnumb=rand.nextInt(size);// saving the random number

                //exchanging the positions of the cards.
                int tmp = deck.get(Integer.valueOf(randnumb));
                int tmp2=deck.get(Integer.valueOf(x));
                deck.set(x,tmp);
                deck.set(randnumb,tmp2);

            }
            return deck;
        }
        else
        {
            return new ArrayList<Integer>();
        }
    }

 }