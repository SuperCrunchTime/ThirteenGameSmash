package com.example.mangaramu.thirteengamesmash;

import java.util.ArrayList;

/**
 * Created by mangaramu on 12/10/2016.
 */

public class GameTurn {
    int player;
    ArrayList<Integer> cardsplayed=new ArrayList<>();
    GameTurn()
    {

    }
    GameTurn(int d, ArrayList<Integer> y)
    {
        player=d;
        if(y!=null)
        {
            cardsplayed.addAll(y);

        }
        else {
            cardsplayed = null;
        }
    }
}
