package com.example.mangaramu.thirteengamesmash;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by mangaramu on 12/9/2016.
 */

public class ClientHuman implements Player {
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
        return false;
    }

    @Override
    public Boolean isClienthuman() {
        return true;
    }

    @Override
    public Boolean isAI() {
        return false;
    }

    public void updatePlayPile(ArrayList<Integer> playPile) {
        this.playPile = playPile;
    }
}
