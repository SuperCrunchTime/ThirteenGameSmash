package com.example.mangaramu.thirteengamesmash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by mangaramu on 12/1/2016.
 */

public class GameState {
    private ArrayList<Player> players;
    private ArrayList<ArrayList<Integer>> playerHands;
    private ThirteenGameLogic gameLogic;
    private ArrayList<Integer> playPile;
    private ArrayList<Integer> cardsSelected;

    public int getStartingplayer() {
        return startingplayer;
    }

    public void setStartingplayer(int startingplayer) {
        this.startingplayer = startingplayer;
    }

    private Map<Integer, Boolean> passedCounter;
    private int playerTurn;
    private boolean gameOver;
    private int startingplayer;
    private  Random f;

    public GameState(int numberofhumans) {
        players = new ArrayList<Player>();
        playerHands = new ArrayList<ArrayList<Integer>>();
        gameLogic = new ThirteenGameLogic(true);
        playPile = new ArrayList<Integer>();
        cardsSelected = new ArrayList<Integer>();
        passedCounter = new HashMap<Integer, Boolean>();
        gameOver=false;
        playerTurn = 0;
        startingplayer=0;// should be whoever has the lowest 3 in their hand!// from 0-3 if 4 players
        initPlayers(numberofhumans);
        initHands();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public void initPlayers(int x) {

        for(int u=0;u<4;u++)
        {
            if(x>0&&u==0)
            {
                players.add(new HostHuman());
            }
            else if(u<x)
            {
                players.add(new ClientHuman());
            }
            else
            {
                players.add(new AI());
            }


        }

    }

    public void initHands() {
        for (int i = 0; i < 4; i++) {
            playerHands.add(new ArrayList<Integer>());
            passedCounter.put(i, false);
        }

        ArrayList<Integer> deck = new ArrayList<Integer>();
        for (int i = 0; i < 52; i++) {
            deck.add(i);
        }
        deck = new ShuffleCards().shuffle(deck);

        for (int i = 0; i < 52; i++) {
            playerHands.get(i / 13).add(deck.get(i));
        }

        for (int i = 0; i < 4; i++) {
            Collections.sort(playerHands.get(i));
        }
    }

    public ArrayList<ArrayList<Integer>> getPlayerHands() {
        return playerHands;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ThirteenGameLogic getGameLogic() {
        return gameLogic;
    }

    public ArrayList<Integer> getPlayPile() {
        return playPile;
    }

    public ArrayList<Integer> getCardsSelected() {
        return cardsSelected;
    }

    public Map<Integer, Boolean> getPassedCounter() {
        return passedCounter;
    }

    public int getPlayerTurn() {
        return playerTurn;
    }


    public void setPlayerHands(ArrayList<ArrayList<Integer>> playerHands) {
        this.playerHands = playerHands;
    }

    public void setGameLogic(ThirteenGameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    public void setPlayPile(ArrayList<Integer> playPile) {
        this.playPile.clear();
        for(int y =0;y<playPile.size();y++)
        {
            this.playPile.add(playPile.get(y));
        }
        Collections.sort(this.playPile);

    }

    public void setCardsSelected(ArrayList<Integer> cardsSelected) {

        this.cardsSelected = cardsSelected;
    }
    public  void addCardsSelected(Integer card)
    {
        cardsSelected.add(card);
    }

    public  void removeCardsSelected(Integer card)
    {
        cardsSelected.remove(card);
    }
    public void setPassedCounter(Integer key, Boolean value) {
        passedCounter.put(key,value);
    }

    public void setPlayerTurn(int playerTurn) {
        this.playerTurn = Integer.valueOf(playerTurn);
    }
    public void clearPassedCounter()
    {
        for(int i = 0;i<4;i++)
        {
            passedCounter.put(i,false);
        }
    }

    public void clearCardsSelected(){
        cardsSelected.clear();
    }

    public void updateStartOfGame(boolean startOfGame) {
        this.gameLogic.updateStartOfGame(startOfGame);
    }
}
