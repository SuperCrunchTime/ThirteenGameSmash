package com.example.mangaramu.thirteengamesmash;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by mangaramu on 11/29/2016.
 */

public class HostHuman implements Player {
    ThirteenGameLogic gl = new ThirteenGameLogic();
    ArrayList<Integer> playPile = new ArrayList<Integer>();
    ArrayList<Integer> newPlayedCards = new ArrayList<Integer>();
    boolean passed = false;
    boolean played = false;


//    @Override
//    public ArrayList<Integer> takeTurn(ArrayList<Integer> playedCards, ArrayList<Integer> cards) {
//        while (!passed && ! played) {
//
//        }
//    }

    public ArrayList<Integer> playCards(ArrayList<Integer> cardsSelected) {
        Collections.sort(cardsSelected);
        if (gl.checkCardsSelected(this.playPile, cardsSelected)) {
            this.newPlayedCards = cardsSelected;
        } else {
            this.newPlayedCards.clear();
        }
        return this.newPlayedCards;
    }


    public ArrayList<Integer> pass() { // to set the pass variable for the AI
        this.newPlayedCards.clear();
        return this.newPlayedCards;
    }

    @Override
    public Boolean isHosthuman() {
        return true;
    }

    @Override
    public Boolean isClienthuman() {
        return false;
    }

    @Override
    public Boolean isAI() {
        return false;
    }

    public void updatePlayPile(ArrayList<Integer> playPile) {
        this.playPile = playPile;
    }


}
