package com.example.mangaramu.thirteengamesmash;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mangaramu on 11/29/2016.
 */

public class ThirteenGameThread extends Thread{

    Object synchobject=new Object();
    boolean rungame=true;
    ThirteenCardsSelected cards= new ThirteenCardsSelected();
    GameState gameState ;
    Handler[] uihandlers; ////0 is gameuihandle 1 is turnstart
    Boolean humanplayorpass=false;
    ArrayList<Integer> nohand;// container for the numbers of the players who have no hand
    ArrayList<Integer> playersnotpassed;// container for the numbers of the players still in the game.
    int gamemode; //0 is singlegame 1 is multigame

    public Boolean getUidone() {
        return uidone;
    }

    public void setUidone(Boolean uidone) {
        this.uidone = uidone;
    }

    Boolean uidone=false;
    ArrayList<Integer>tmphand;


    Handler isfinnished = new Handler(){// gets
        @Override
        public void handleMessage(Message msg) {
           rungame=(Boolean)msg.obj; /// message from gameui should be a boolean that we set.. then when we are done we set it back to the default
        }
    };



    Handler[] gamethreadhandlers = {isfinnished};

    public ThirteenGameThread(Handler[] x,int gamode,int humanplayers)
    {
        gamemode=gamode;
        uihandlers=x;
        gameState =new GameState(humanplayers);
    }// constructor for the gamethreadclass

    public Handler[] gethandlers()
    {
        return gamethreadhandlers;
    }


    public Boolean getHumanplayorpass() {
        return humanplayorpass;
    }

    public void setHumanplayorpass(ArrayList<Integer> hand) { // takes in the hand to be played by the player and sets it to temp hand
        //sets humanplayorpass to true to escape the waiting while loop.
        tmphand=hand;
        humanplayorpass = true;
    }

    @Override
    public void run()
    {
            singlegame();

    }

    void stopgame()
    {
        humanplayorpass=true;
        rungame=false;

    }
    private void sendmessageto(Handler x,Object f)// method to send messages with a object to a handler
    {
        Message m = Message.obtain();
        m.obj=f;
        m.setTarget(x);
        m.sendToTarget();
    }

    public GameState getGameState() {
        return this.gameState;
    }

    /*private ThirteenGameLogic getThirteenGameLogic() // should only be called after gameinit is called
    {
    return gameLogic;
    }*/

    private void AIturn(int d)// handles the ai's turn. takes in an int variable that is the turn number. As the turn number is used to get hand and player information
    {
        if (nohand.contains(Integer.valueOf(d))) // if ai does not have anything
        {
            playersnotpassed.remove(Integer.valueOf(d)); // player passed so they are removed from the players still in the round
        }
        else// if the ai has cards in its hand
        {
            tmphand = ((AI) gameState.getPlayers().get(d)).takeTurn(gameState.getPlayPile(), gameState.getPlayerHands().get(d));
                           /* if(tmphand.size()==1 && !gameState.getPlayerHands().get(d).remove(Integer.valueOf(tmphand.get(0))))
                            {
                                Log.d("SERIOUS ERROR OCCOURED","At value of d: " +Integer.toString(d)+ "Boolean :" +gameState.getPlayers().get(d).isAI());
                                throw new NumberFormatException();
                            }*/
            if (tmphand.size() == 0)// if the ai did not play anything then we should pass the ai player
            {
                playersnotpassed.remove(Integer.valueOf(d)); // player passed so they are removed from the players still in the round
                gameState.setPassedCounter(d,true);// set the passsed counter hashmap entry corresponding to the integer d to true
                sendmessageto(uihandlers[0],"");//sends gamestate to the uiupdater handler
                while(!uidone) //wait for notificaiton that ui updated
                {
                    android.os.SystemClock.sleep(100);
                }
                android.os.SystemClock.sleep(1100);
                uidone=false;
            }
            else if(tmphand.contains(48)&&tmphand.contains(49)&&tmphand.contains(50)&&tmphand.contains(51))// special card hand case
            {
                gameState.setPlayPile(tmphand);// set the playpile with the cards played
                gameState.getPlayerHands().get(d).clear();// instant win condition so clear players hand

                sendmessageto(uihandlers[0],"");//sends gamestate to the uiupdater handler
                while(!uidone) //wait for notificaiton that ui updated
                {
                    android.os.SystemClock.sleep(100);
                }

                android.os.SystemClock.sleep(1100);

                uidone=false;
                gameState.clearCardsSelected();// clears the cards that were selected
                playersnotpassed.remove(Integer.valueOf(d)); // player has no hand so they are removed from the players still in the round
                nohand.add(d);
                gameState.setPassedCounter(d,true);// the player is not in the game anymore so the default is to pass their turn
            }
            else // the ai played something! we need to handle this!
            {

                gameState.setPlayPile(tmphand);// set the playpile with the cards played
                for (int r = 0; r < tmphand.size(); r++)// removes the cards played from the ai's hand
                {
                    gameState.getPlayerHands().get(d).remove(Integer.valueOf(tmphand.get(r)));// want to remove the object and not the object at index!
                }


                sendmessageto(uihandlers[0],"");//sends gamestate to the uiupdater handler

                while(!uidone) //wait for notificaiton that ui updated
                {
                    android.os.SystemClock.sleep(100);
                }

                android.os.SystemClock.sleep(1100);

                uidone=false;
                gameState.clearCardsSelected();// clears the cards that were selected

                if (gameState.getPlayerHands().get(d).size() == 0) // seccond check to see if ai has anything
                {
                    if(!nohand.contains(Integer.valueOf(d)))//if nohand does not cotina the player.. add the player to nohand
                    {
                        playersnotpassed.remove(Integer.valueOf(d)); // player has no hand so they are removed from the players still in the round
                        nohand.add(d);
                    }

                    gameState.setPassedCounter(d,true);// the player is not in the game anymore so the default is to pass their turn
                    //TODO maybe implement placings ofd who won and store in arraylist to be given to a listview?
                    //TODO and remove the player? implement player counts going down if they are now out of the game?!
                }
            }

        }
    }
    private void Playerturn(int d)// handles the player's turn. takes in an int variable that is the turn number. As the turn number is used to get hand and player information
    {
        if (nohand.contains(Integer.valueOf(d))) // if the player  does not have anything
        {
            playersnotpassed.remove(Integer.valueOf(d)); // player passed so they are removed from the players still in the round
        }
        else //human player has cards
        {
            sendmessageto(uihandlers[1],"");// sends an empty message that notifys gamestart
            while(humanplayorpass==false )// once we break out of this loop, tmphand will be set to something!
            {
                android.os.SystemClock.sleep(100);
            }
            //TODO code to know weather or not the human played anything

            if (tmphand.size() == 0)// if the player did not play anything then we should pass the human player
            {
                playersnotpassed.remove(Integer.valueOf(d)); // player has no hand so they are removed from the players still in the round
                sendmessageto(uihandlers[0],"");//sends gamestate to the uiupdater handler

                while(!uidone) //wait for notificaiton that ui updated
                {
                    android.os.SystemClock.sleep(100);
                }
                uidone=false;
                android.os.SystemClock.sleep(1100);
                gameState.setPassedCounter(d,true);// set the passsed counter hashmap entry corresponding to the integer d to true
            }
            else if(tmphand.contains(48)&&tmphand.contains(49)&&tmphand.contains(50)&&tmphand.contains(51))// special card hand case
            {
                gameState.setPlayPile(tmphand);// set the playpile with the cards played
                gameState.getPlayerHands().get(d).clear();// instant win condition so clear players hand

                sendmessageto(uihandlers[0],"");//sends gamestate to the uiupdater handler
                while(!uidone) //wait for notificaiton that ui updated
                {
                    android.os.SystemClock.sleep(100);
                }

                android.os.SystemClock.sleep(1100);
                uidone=false;
                gameState.clearCardsSelected();// clears the cards that were selected
                playersnotpassed.remove(Integer.valueOf(d)); // player has no hand so they are removed from the players still in the round
                nohand.add(d);
                gameState.setPassedCounter(d,true);// the player is not in the game anymore so the default is to pass their turn
            }
            else // the human played something! we need to handle this!
            {
                gameState.setPlayPile(tmphand);// set the playpile with the cards played
                for (int r = 0; r < tmphand.size(); r++)// removes the cards played from the human's hand and from cards selected
                {
                    gameState.getPlayerHands().get(d).remove(Integer.valueOf(tmphand.get(r)));// want to remove the object and not the object at index!
                }
                gameState.updateStartOfGame(false);

                sendmessageto(uihandlers[0],"");//sends gamestate to the uiupdater handler

                while(!uidone) //wait for notificaiton that ui updated
                {
                    android.os.SystemClock.sleep(100);
                }
                android.os.SystemClock.sleep(1100);
                uidone=false;
                gameState.clearCardsSelected();// clears the cards that were selected

                if (gameState.getPlayerHands().get(d).size() == 0) // seccond time we check for player out of game
                {
                    if(!nohand.contains(Integer.valueOf(d)))//if nohand does not cotina the player.. add the player to nohand
                    {
                        playersnotpassed.remove(Integer.valueOf(d)); // player has no hand so they are removed from the players still in the round
                        nohand.add(d);
                    }

                    gameState.setPassedCounter(d,true); // the player is not in the game anymore so the default is to pass their turn
                    //TODO maybe implement placings ofd who won and store in arraylist to be given to a listview?
                    //TODO and remove the player? implement player counts going down if they are now out of the game?!
                }

            }

        }


        humanplayorpass=false;//human has played and or pased...
    }
    private void Clientturn(int d)
    {
        if (nohand.contains(Integer.valueOf(d))) // if the player  does not have anything
        {
            playersnotpassed.remove(Integer.valueOf(d)); // player passed so they are removed from the players still in the round
        }
        else //human player has cards
        {
            sendmessageto(uihandlers[5],"");// sends an empty message that notifys gamestart of client
            while(humanplayorpass==false )// once we break out of this loop, tmphand will be set to something!
            {
                android.os.SystemClock.sleep(100);
            }
            //TODO code to know weather or not the human played anything

            if (tmphand.size() == 0)// if the player did not play anything then we should pass the human player
            {
                playersnotpassed.remove(Integer.valueOf(d)); // player has no hand so they are removed from the players still in the round
                sendmessageto(uihandlers[0],"");//sends gamestate to the uiupdater handler
                while(!uidone) //wait for notificaiton that ui updated
                {
                    android.os.SystemClock.sleep(100);
                }

                android.os.SystemClock.sleep(1100);

                uidone=false;
                gameState.setPassedCounter(d,true);// set the passsed counter hashmap entry corresponding to the integer d to true
            }
            else if(tmphand.contains(48)&&tmphand.contains(49)&&tmphand.contains(50)&&tmphand.contains(51))// special card hand case
            {
                gameState.setPlayPile(tmphand);// set the playpile with the cards played
                gameState.getPlayerHands().get(d).clear();// instant win condition so clear players hand

                sendmessageto(uihandlers[0],"");//sends gamestate to the uiupdater handler
                while(!uidone) //wait for notificaiton that ui updated
                {
                    android.os.SystemClock.sleep(100);
                }

                android.os.SystemClock.sleep(1100);

                uidone=false;
                gameState.clearCardsSelected();// clears the cards that were selected
                playersnotpassed.remove(Integer.valueOf(d)); // player has no hand so they are removed from the players still in the round
                nohand.add(d);
                gameState.setPassedCounter(d,true);// the player is not in the game anymore so the default is to pass their turn
            }
            else // the human played something! we need to handle this!
            {
                gameState.setPlayPile(tmphand);// set the playpile with the cards played
                for (int r = 0; r < tmphand.size(); r++)// removes the cards played from the human's hand and from cards selected
                {
                    gameState.getPlayerHands().get(d).remove(Integer.valueOf(tmphand.get(r)));// want to remove the object and not the object at index!
                }
                gameState.updateStartOfGame(false);

                sendmessageto(uihandlers[0],"");//sends gamestate to the uiupdater handler

                while(!uidone) //wait for notificaiton that ui updated
                {
                    android.os.SystemClock.sleep(100);
                }
                android.os.SystemClock.sleep(1100);
                uidone=false;

                gameState.clearCardsSelected();// clears the cards that were selected

                if (gameState.getPlayerHands().get(d).size() == 0) // seccond time we check for player out of game
                {
                    if(!nohand.contains(Integer.valueOf(d)))//if nohand does not cotina the player.. add the player to nohand
                    {
                        playersnotpassed.remove(Integer.valueOf(d)); // player has no hand so they are removed from the players still in the round
                        nohand.add(d);
                    }

                    gameState.setPassedCounter(d,true); // the player is not in the game anymore so the default is to pass their turn
                    //TODO maybe implement placings ofd who won and store in arraylist to be given to a listview?
                    //TODO and remove the player? implement player counts going down if they are now out of the game?!
                }

            }

        }
        humanplayorpass=false;//human has played and or pased...
    }
    private void singlegame()
    {
        for(int g=0;g<4 ;g++)// checks who has the 3 of spades at game startup!
        {
            if(gameState.getPlayerHands().get(g).contains(Integer.valueOf(0)))
            {
                gameState.setStartingplayer(g);// from 0-3
                break;
            }
        }

        nohand=new ArrayList<Integer>();// container for the numbers of the players who have no hand
        playersnotpassed= new ArrayList<Integer>();// container for the numbers of the players still in the game.

        while(rungame)
        {
            int turncounter=0;
            playersnotpassed.clear();
            playersnotpassed.add(0);
            playersnotpassed.add(1);
            playersnotpassed.add(2);
            playersnotpassed.add(3);


            while(playersnotpassed.size()>1 && rungame && nohand.size()<3) // while loop for the entire round evrytime the player passes
            // they get removed from the round! checks to see if rungame and if there are at least 2 players with hands
            {

                int d = (turncounter+gameState.getStartingplayer())%4;// allows round to start with different starting players
                gameState.setPlayerTurn(d);
                tmphand=new ArrayList<Integer>();//remake the temphand for each turn
                if(gameState.getPassedCounter().get(d))// if this player has passed previously
                {

                }
                else // this player has not passed so now they can play
                {

                    if (gameState.getPlayers().get(d).isAI())  // if the player is an ai player
                    {
                        AIturn(d);//code for the ai's turn
                        if (nohand.size() ==3)
                        {
                            break;

                        }
                    }
                    else if(gameState.getPlayers().get(d).isHosthuman())// the player is a humanhost player
                    {
                        Playerturn(d);//code for the human's turn
                        if (nohand.size() ==3)
                        {
                            break;

                        }
                    }
                    else if(gameState.getPlayers().get(d).isClienthuman())// the player is a humanclient player
                    {
                        Clientturn(d);//code for the client's turn

                        if (nohand.size() ==3)
                        {
                            break;

                        }
                    }

                }
                tmphand=new ArrayList<Integer>();//remake the temphand for each turn
                turncounter++;
            }
            //TODO set a winner if first person to clear entire hand!

            // handle people passing, people without hands, and when only one person has a hand left.
            if (nohand.size() ==3)
            {
                sendmessageto(uihandlers[2],"");//activates the gameover handler
                gameState.isGameOver();
                stopgame();
                break;
            }

            gameState.setStartingplayer(playersnotpassed.get(0));
            gameState.setPlayPile(new ArrayList<Integer>()); // make a new play pile as there was a winner of the round!
            gameState.clearPassedCounter();
            sendmessageto(uihandlers[0],"NR");//sends gamestate to the uiupdater handler
            while(!uidone) //wait for notificaiton that ui updated
            {
                android.os.SystemClock.sleep(100);
            }
            uidone=false;

            //sendmessageto(uihandlers[0],gameLogic); sends gamelogic(ThirteenGameLogic) to gameuihandls
            //   sendmessageto(uihandlers[1],cards); sends to cards(ThirteenCardsSelected) to carduihandle

        }
    }
    private void multigame()
    {

    }
}
