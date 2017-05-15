package com.example.mangaramu.thirteengamesmash;

import android.app.Fragment;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mangaramu on 11/29/2016.
 */

public class GameUI extends Fragment {


    Map<Integer, Integer> cardMapping = new ThirteenGameLogic().generateCardMapping();

    ViewGroup.MarginLayoutParams cardMargins;

    ThirteenGameThread newGame; //our gamethread object
    ArrayList<ImageView> cardImageViewsInHand;
    ArrayList<ImageView> cardImageViewsInPlay;
    ArrayList<ArrayList<Integer>> playerHands;
    ArrayList<BluetoothIOThread> ClientConnectedIOThreads = new ArrayList<BluetoothIOThread>();
    BluetoothIOThread HostConnectedIOThread;

    final Map<Integer, Boolean> shiftedUp = new HashMap<Integer, Boolean>();

    View v;
    Button play, pass, menu;
    ListView playerHand;
    ListView recentPlay;
    RelativeLayout player2left;
    RelativeLayout player2front;
    RelativeLayout player2right;
    Drawable dbActive = new ColorDrawable(Color.YELLOW);
    Drawable dbPassive = new ColorDrawable(Color.TRANSPARENT);


    TextView player1;
    TextView player2;
    TextView player3;
    TextView player4;
    int gamemode = 0; //0 for singleplay 1 for host multiplayer 2 for client multiplayer
    int playernumber = 0;// assigns specific play number
    int HumanPlayers = 1;
    Boolean clientstartboolean = false;

    View card1, card2;
    int cardNumber;
    gamebutton f;

    public GameUI() { //default constructor

        setRetainInstance(true);
    }

    Handler gameuihandle = new Handler() // handler for the game ui stuff;
    {
        @Override
        public void handleMessage(Message msg) {// handler for gameui updates
            //TODO Handling multiplayer gamemode!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            if (gamemode == 1)// if we are in hostmode we send a message
            {
                for (int y = 0; y < ClientConnectedIOThreads.size(); y++) {
                    StringBuilder f = new StringBuilder();
                    f.append("UG").append("-").append(newGame.gameState.getPlayerTurn()).append("-").append("PP").append("-");
                    if (((String) msg.obj).equals("NR"))// if there is a new round!
                    {
                        f.append("NR").append("-");
                    }
                    else {
                        for (int w = 0; w < newGame.gameState.getPlayPile().size(); w++) {
                            if (w == newGame.gameState.getPlayPile().size() - 1) {
                                f.append(newGame.gameState.getPlayPile().get(w));
                            } else {
                                f.append(newGame.gameState.getPlayPile().get(w)).append("-");
                            }
                        }
                    }


                    f.append("\r\n");//end of line
                    ClientConnectedIOThreads.get(y).write(f.toString());//send the built string to all the clients!
                    //write message that starts a gameuiupdate!
                }
            } else if (gamemode == 2)//if we are in clientmode we handle a recieved message
            {
                newGame.gameState.getGameLogic().updateStartOfGame(false);
                GameTurn t = (GameTurn) msg.obj;
                newGame.gameState.setPlayerTurn(t.player);// set the player turn for who we recieved the update for
                if (t.cardsplayed == null)  {
                    newGame.gameState.setPlayPile(new ArrayList<Integer>());
                } else if (t.cardsplayed.size() == 0) {

                } else {
                    newGame.gameState.setPlayPile(t.cardsplayed);// set the playpile of client.
                }

            }
            //TODO End Handling multiplayer gamemode!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            updategameui();
        }
    };
    Handler turnstart = new Handler() { //handler for player turn start
        @Override
        public void handleMessage(Message msg) {
            //TODO  Handling multiplayer gamemode!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

            init_turn();

            //TODO End Handling multiplayer gamemode!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        }
    }; //to handle when the UI turn begins
    Handler gameover = new Handler() {  //handler for endgame
        @Override
        public void handleMessage(Message msg) {

            f.gamebuttonclick(0);
            //TODO  Handling multiplayer gamemode!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            if (gamemode == 1)// have the host send a message to client that the game is over
            {
                for (int y = 0; y < ClientConnectedIOThreads.size(); y++) {
                    StringBuilder g = new StringBuilder();
                    g.append("EG").append("-").append("\r\n");
                    ClientConnectedIOThreads.get(y).write(g.toString());
                }

            }
            //TODO end Handling multiplayer gamemode!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        }
    };// to handle when the game is over
    Handler setplayernumb = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            setPlayernumber((int) msg.obj);
            player1.setText(getResources().getString(R.string.player) + " " + String.format("%d", ((playernumber) % 4) + 1));// set the player numbers based on the player of the device's number
            player2.setText(getResources().getString(R.string.player) + " " + String.format("%d", ((playernumber+1) % 4) + 1));
            player3.setText(getResources().getString(R.string.player) + " " + String.format("%d", ((playernumber+2) % 4) + 1));
            player4.setText(getResources().getString(R.string.player) + " " + String.format("%d", ((playernumber+3) % 4) + 1));
        }
    };// to handle settin the player number of a player

    Handler setplayerHand = new Handler()// handles setting the clients hand
    {
        @Override
        public void handleMessage(Message msg) {
            clientstartboolean = true;
            newGame.gameState.getPlayerHands().get(playernumber).clear();// clears the initial hand
            newGame.gameState.getPlayerHands().get(playernumber).addAll((ArrayList<Integer>) msg.obj);// adds all the ints to the hand
            updategameui();
        }
    };

    //TODO Handling multiplayer gamemode!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    Handler Clienturnstart = new Handler()// handles starting the clients turn
    {
        @Override
        public void handleMessage(Message msg) {
            StringBuilder f = new StringBuilder();
            f.append("ST").append("-").append("\r\n");
            ClientConnectedIOThreads.get(newGame.gameState.getPlayerTurn() - 1).write(f.toString());//playerturn-1 should be the number of th eclient in the clientthreads array
        }
    };
    //TODO  end Handling multiplayer gamemode!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    //TODO Handling multiplayer gamemode!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    Handler Clientplayed = new Handler() //handles when the client plays
    {
        @Override
        public void handleMessage(Message msg) {
            ArrayList<Integer> tmpo;
            tmpo = (ArrayList<Integer>) msg.obj;
            newGame.setHumanplayorpass(tmpo);// set humanplayorpassed for client
        }
    };
    //TODO End Handling multiplayer gamemode!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


    Handler[] Uitalk = {gameuihandle, turnstart, gameover, setplayernumb, setplayerHand, Clienturnstart, Clientplayed}; // Uitalk object


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            f = (gamebutton) getActivity();
        } catch (ClassCastException e) {
            Log.d("Class Exception", "Attaching class needs to implement the gamebutton interface ");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void initCardImageViews() {

        cardImageViewsInHand = new ArrayList<ImageView>();
        cardImageViewsInPlay = new ArrayList<ImageView>();

        ImageView card1 = (ImageView) v.findViewById(R.id.card1);
        ImageView card2 = (ImageView) v.findViewById(R.id.card2);
        ImageView card3 = (ImageView) v.findViewById(R.id.card3);
        ImageView card4 = (ImageView) v.findViewById(R.id.card4);
        ImageView card5 = (ImageView) v.findViewById(R.id.card5);
        ImageView card6 = (ImageView) v.findViewById(R.id.card6);
        ImageView card7 = (ImageView) v.findViewById(R.id.card7);
        ImageView card8 = (ImageView) v.findViewById(R.id.card8);
        ImageView card9 = (ImageView) v.findViewById(R.id.card9);
        ImageView card10 = (ImageView) v.findViewById(R.id.card10);
        ImageView card11 = (ImageView) v.findViewById(R.id.card11);
        ImageView card12 = (ImageView) v.findViewById(R.id.card12);
        ImageView card13 = (ImageView) v.findViewById(R.id.card13);

        card1.setVisibility(View.VISIBLE);
        card2.setVisibility(View.VISIBLE);
        card3.setVisibility(View.VISIBLE);
        card4.setVisibility(View.VISIBLE);
        card5.setVisibility(View.VISIBLE);
        card6.setVisibility(View.VISIBLE);
        card7.setVisibility(View.VISIBLE);
        card8.setVisibility(View.VISIBLE);
        card9.setVisibility(View.VISIBLE);
        card10.setVisibility(View.VISIBLE);
        card11.setVisibility(View.VISIBLE);
        card12.setVisibility(View.VISIBLE);
        card13.setVisibility(View.VISIBLE);

        ImageView cardp1 = (ImageView) v.findViewById(R.id.play_pile1);
        ImageView cardp2 = (ImageView) v.findViewById(R.id.play_pile2);
        ImageView cardp3 = (ImageView) v.findViewById(R.id.play_pile3);
        ImageView cardp4 = (ImageView) v.findViewById(R.id.play_pile4);
        ImageView cardp5 = (ImageView) v.findViewById(R.id.play_pile5);
        ImageView cardp6 = (ImageView) v.findViewById(R.id.play_pile6);
        ImageView cardp7 = (ImageView) v.findViewById(R.id.play_pile7);
        ImageView cardp8 = (ImageView) v.findViewById(R.id.play_pile8);
        ImageView cardp9 = (ImageView) v.findViewById(R.id.play_pile9);
        ImageView cardp10 = (ImageView) v.findViewById(R.id.play_pile10);
        ImageView cardp11 = (ImageView) v.findViewById(R.id.play_pile11);
        ImageView cardp12 = (ImageView) v.findViewById(R.id.play_pile12);
        ImageView cardp13 = (ImageView) v.findViewById(R.id.play_pile13);

        cardImageViewsInHand.add(card1);
        cardImageViewsInHand.add(card2);
        cardImageViewsInHand.add(card3);
        cardImageViewsInHand.add(card4);
        cardImageViewsInHand.add(card5);
        cardImageViewsInHand.add(card6);
        cardImageViewsInHand.add(card7);
        cardImageViewsInHand.add(card8);
        cardImageViewsInHand.add(card9);
        cardImageViewsInHand.add(card10);
        cardImageViewsInHand.add(card11);
        cardImageViewsInHand.add(card12);
        cardImageViewsInHand.add(card13);

        cardImageViewsInPlay.add(cardp1);
        cardImageViewsInPlay.add(cardp2);
        cardImageViewsInPlay.add(cardp3);
        cardImageViewsInPlay.add(cardp4);
        cardImageViewsInPlay.add(cardp5);
        cardImageViewsInPlay.add(cardp6);
        cardImageViewsInPlay.add(cardp7);
        cardImageViewsInPlay.add(cardp8);
        cardImageViewsInPlay.add(cardp9);
        cardImageViewsInPlay.add(cardp10);
        cardImageViewsInPlay.add(cardp11);
        cardImageViewsInPlay.add(cardp12);
        cardImageViewsInPlay.add(cardp13);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {


        //TODO Handling multiplayer gamemode!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        if (gamemode == 1)// set the handlers for host connections to the client!
        {
            HumanPlayers = HumanPlayers + ClientConnectedIOThreads.size();//add the number of client connected threads to the number of Human players
            for (int y = 0; y < ClientConnectedIOThreads.size(); y++) {
                ClientConnectedIOThreads.get(y).setHandlerArr(Uitalk);
            }
        }
        if (gamemode == 2)// set handlers for Client connection to host
        {
            HostConnectedIOThread.setHandlerArr(Uitalk);
        }
        //TODO End of Handling multiplayer gamemode!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        v = inflater.inflate(R.layout.fraggame, container, false);
        play = (Button) v.findViewById(R.id.playbutt);
        pass = (Button) v.findViewById(R.id.passbutt);
        menu = (Button) v.findViewById(R.id.menubutton);
        player1 = (TextView) v.findViewById(R.id.player1);
        player2 = (TextView) v.findViewById(R.id.player2);
        player3 = (TextView) v.findViewById(R.id.player3);
        player4 = (TextView) v.findViewById(R.id.player4);
        player2front = (RelativeLayout) v.findViewById(R.id.player2front);
        player2left = (RelativeLayout) v.findViewById(R.id.player2left);
        player2right = (RelativeLayout) v.findViewById(R.id.player2right);
        newGame = new ThirteenGameThread(Uitalk, gamemode, HumanPlayers);// creation of the gamethread

        playerHands = newGame.getGameState().getPlayerHands(); // initilize the game and get the player hands
        initCardImageViews();

if (gamemode !=2) {
    player1.setText(getResources().getString(R.string.player) + " " + String.format("%d", ((playernumber) % 4) + 1));// set the player numbers based on the player of the device's number
    player2.setText(getResources().getString(R.string.player) + " " + String.format("%d", ((playernumber+1) % 4) + 1));
    player3.setText(getResources().getString(R.string.player) + " " + String.format("%d", ((playernumber+2) % 4) + 1));
    player4.setText(getResources().getString(R.string.player) + " " + String.format("%d", ((playernumber+3) % 4) + 1));
}

//        ViewGroup.MarginLayoutParams cardMargins = (ViewGroup.MarginLayoutParams) CardsInHand.get(0).getLayoutParams();
//        cardMargins.topMargin = 20;
//        CardsInHand.get(0).setLayoutParams(cardMargins);

//        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) CardsInHand.get(0).getLayoutParams();
//        lp.topMargin = lp.topMargin - 20;
//        CardsInHand.get(0).setLayoutParams(lp);

        for (int i = 0; i < 13; i++) {
            shiftedUp.put(i, false);
        }

        for (int i = 0; i < 13; i++)//loop to set onclick listeners for all the cards in our hand
        {
            cardNumber = i;
            cardImageViewsInHand.get(i).setOnClickListener(new View.OnClickListener() {
                int temp = cardNumber;

                @Override
                public void onClick(View v) {
                    cardMargins = (ViewGroup.MarginLayoutParams) cardImageViewsInHand.get(temp).getLayoutParams();
                    //TransitionManager.beginDelayedTransition(container);
                    if (!shiftedUp.get(temp)) {
                        cardMargins.bottomMargin = cardMargins.bottomMargin + 20;
                        shiftedUp.put(temp, true);
                        newGame.gameState.addCardsSelected(newGame.gameState.getPlayerHands().get(playernumber).get(temp));//gets card associated with the tmp number
                        if (newGame.gameState.getGameLogic().checkCardsSelected(newGame.gameState.getPlayPile(), newGame.gameState.getCardsSelected())) {
                            play.setClickable(true);
                            play.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                        } else {
                            play.setClickable(false);
                            play.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);

                        }
                    } else {
                        cardMargins.bottomMargin = cardMargins.bottomMargin - 20;
                        shiftedUp.put(temp, false);
                        newGame.gameState.removeCardsSelected(newGame.gameState.getPlayerHands().get(playernumber).get(temp));//removes card associated with the tmp number
                        if (newGame.gameState.getGameLogic().checkCardsSelected(newGame.gameState.getPlayPile(), newGame.gameState.getCardsSelected()))//checks the cards selected to
                        //// see if they are playable, and sets the play button to clickable if they are
                        {
                            play.setClickable(true);
                            play.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                        } else {
                            play.setClickable(false);
                            play.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                        }
                    }
                    cardImageViewsInHand.get(temp).setLayoutParams(cardMargins);
                }

            });
            cardImageViewsInHand.get(i).setBackgroundResource(cardMapping.get(playerHands.get(playernumber).get(i)));//getting the value of the card that maps to the resource id of the card image tied to the value

            //TODO Handling multiplayer gamemode!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            if (gamemode == 2) {
                cardImageViewsInHand.get(i).setVisibility(View.GONE);// set the card view to gone! because we are the client awaiting cards from the host
            }
            //TODO End of Handling multiplayer gamemode!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        }


        play.setOnClickListener(new View.OnClickListener() {// the gamethread handles card removal from hand after play!
            @Override
            public void onClick(View v) {
                for (int i = 0; i < newGame.gameState.getPlayerHands().get(playernumber).size(); i++) {// set the cards to not be clickable
                    //also set the play and pass buttons to not be clickable after play
                    if (shiftedUp.get(i)) {
                        cardMargins = (ViewGroup.MarginLayoutParams) cardImageViewsInHand.get(i).getLayoutParams();
                        cardMargins.bottomMargin = cardMargins.bottomMargin - 20;
                        cardImageViewsInHand.get(i).setLayoutParams(cardMargins);
                        shiftedUp.put(i, false);
                    }
                    cardImageViewsInHand.get(i).setClickable(false);

                }
                play.setClickable(false);
                pass.setClickable(false);
                play.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                pass.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);


                //TODO Handling multiplayer gamemode!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                //// section for handling different gamemode IO
                if (gamemode != 2) {
                    newGame.setHumanplayorpass(newGame.gameState.getCardsSelected());//
                } else if (gamemode == 2) {
                    StringBuilder g = new StringBuilder();
                    g.append("ET").append("-");
                    for (int y = 0; y < newGame.gameState.getCardsSelected().size(); y++) {
                        if (y == newGame.gameState.getCardsSelected().size() - 1) {
                            g.append(newGame.gameState.getCardsSelected().get(y));
                        } else {
                            g.append(newGame.gameState.getCardsSelected().get(y)).append("-");
                        }
                        newGame.gameState.getPlayerHands().get(playernumber).remove(newGame.gameState.getCardsSelected().get(y));//removes card from client hand
                    }
                    g.append("\r\n");
                    HostConnectedIOThread.write(g.toString());
                    newGame.gameState.clearCardsSelected();// mannualy clear the cards selected
                    //send updates to the host
                }
                //TODO End of Handling multiplayer gamemode!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

                not_turn();
            }

        });

        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < newGame.gameState.getPlayerHands().get(playernumber).size(); i++) {// put the cards back down and set them to not be clickable
                    //also set the play and pass buttons to not be clickable after pass
                    if (shiftedUp.get(i)) {
                        cardMargins = (ViewGroup.MarginLayoutParams) cardImageViewsInHand.get(i).getLayoutParams();
                        cardMargins.bottomMargin = cardMargins.bottomMargin - 20;
                        cardImageViewsInHand.get(i).setLayoutParams(cardMargins);
                        shiftedUp.put(i, false);
                    }

                    cardImageViewsInHand.get(i).setClickable(false);


                }
                play.setClickable(false);
                pass.setClickable(false);
                play.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                pass.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

                //TODO Handling multiplayer gamemode!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                if (gamemode != 2) {
                    newGame.setHumanplayorpass(new ArrayList<Integer>());// give the cards selected!
                } else if (gamemode == 2) {
                    StringBuilder g = new StringBuilder();
                    g.append("ET").append("-");
                    g.append("\r\n");
                    HostConnectedIOThread.write(g.toString());
                    newGame.gameState.clearCardsSelected();// mannualy clear the cards selected
                }
                not_turn();// sets up code to show it is not our turn
                //FIXME CODE TO HANDLE DIFFRENT PASSES ONE FOR HOST AND ONE FOR CLIENT
                //TODO End of Handling multiplayer gamemode!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                f.gamebuttonclick(2);

            }

        });
//        card1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("Something happened","something number 1");
//
//            }
//        });

        not_turn();// initially assume it is not our turn
        play.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        pass.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

        //TODO code for diffrent gamemodes
        //TODO Handling multiplayer gamemode!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        if (gamemode == 0)//If we are not the client we start the game
        {
            newGame.start();
        } else if (gamemode == 1) {
            init_Client();
            newGame.start();
        }
        //TODO End of Handling multiplayer gamemode!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        newGame.stopgame();
        gameuihandle = null;
        turnstart = null;
        gameover = null;

        //handle the closing of the connection threads
        if (ClientConnectedIOThreads != null) {
            for (int o = 0; o < ClientConnectedIOThreads.size(); o++) // close the open scokets we obtain from elsewhere if we have any!
            {
                ClientConnectedIOThreads.get(o).cancel();
            }
            ClientConnectedIOThreads.clear();
        }
        if (HostConnectedIOThread != null) {//handle the closing of the connection threads
            HostConnectedIOThread.cancel();
        }


        super.onDetach();
        //players.clear();
        //playerHands.clear();
        //deck.clear();
        //cardImageViewsInHand.clear();
    }

    public interface gamebutton { // interface for handling menu selects
        void gamebuttonclick(int x);
    }

    //@RequiresApi(api = Build.VERSION_CODES.M)
    public void updategameui()//should update the player's cards and the playpile and should only be called if there is something to change!  //TODO -- needs code for when things get played!
    {

        for (int i = 0; i < 13; i++) {
            if (newGame.gameState.getPlayerTurn() == playernumber || clientstartboolean == true) {// if it is our turn then update our ui stuff

                if (i < newGame.gameState.getPlayerHands().get(playernumber).size())// sets card image and visibility of playerhand cards
                {
                    cardImageViewsInHand.get(i).setBackgroundResource(cardMapping.get(newGame.getGameState().getPlayerHands().get(playernumber).get(i)));//getting the value of the card that maps to the resource id of the card image tied to the value
                    cardImageViewsInHand.get(i).setVisibility(View.VISIBLE);
                } else {
                    cardImageViewsInHand.get(i).setVisibility(View.GONE);
                }
            }
            if (i < newGame.gameState.getPlayPile().size())// sets card image and visibility of play pilecards
            {
                cardImageViewsInPlay.get(i).setVisibility(View.VISIBLE);
                cardImageViewsInPlay.get(i).setBackgroundResource(cardMapping.get(newGame.getGameState().getPlayPile().get(i)));//getting the value of the card that maps to the resource id of the card image tied to the value

            } else {
                cardImageViewsInPlay.get(i).setVisibility(View.GONE);

            }

        }
        clientstartboolean = false;


        System.out.println("player: " + newGame.gameState.getPlayerTurn());

        if (newGame.gameState.getPlayerTurn() == ((playernumber+1)%4)) {  //FIXME reinstate these when done with device
            player2left.setAlpha((float) 1);
            player2front.setAlpha((float) .2);
            player2right.setAlpha((float) 1);
        } else if (newGame.gameState.getPlayerTurn() == ((playernumber+2)%4)) {
            player2left.setAlpha((float) 1);
            player2front.setAlpha((float) 1);
            player2right.setAlpha((float) .2);
        } else if (newGame.gameState.getPlayerTurn() == ((playernumber+3)%4)) {
            player2left.setAlpha((float) 1);
            player2front.setAlpha((float) 1);
            player2right.setAlpha((float) 1 );
        }else if (newGame.gameState.getPlayerTurn() == ((playernumber+4)%4)) {
            player2left.setAlpha((float) .2);
            player2front.setAlpha((float) 1);
            player2right.setAlpha((float) 1);
        }



//        ArrayList<ArrayList<Integer>> tmpplayerHands = d.playerHands;
//        ArrayList<Integer> tmpcardsplayed = d.cardsplayed;
//        int tmpplayer = d.player;

        newGame.setUidone(true);
    }

    // @RequiresApi(api = Build.VERSION_CODES.M)
    public void init_turn()//initilizes the turn, making all buttons clickable again
    {
        play.setClickable(false);
        pass.setClickable(true);
        play.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        pass.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);


        player2left.setAlpha((float) 1);
        player2front.setAlpha((float) 1);
        player2right.setAlpha((float) 1);

        for (int i = 0; i < newGame.gameState.getPlayerHands().get(playernumber).size(); i++) {// put the cards back down and set them to not be clickable
            //also set the play and pass buttons to not be clickable after pass


            cardImageViewsInHand.get(i).setClickable(true);


        }
    }

    public void not_turn()//initilizes the turn, making all buttons not clickable
    {
        play.setClickable(false);
        pass.setClickable(false);
        for (int i = 0; i < newGame.gameState.getPlayerHands().get(playernumber).size(); i++) {// put the cards back down and set them to not be clickable
            //also set the play and pass buttons to not be clickable after pass


            cardImageViewsInHand.get(i).setClickable(false);


        }
    }

    public void newnewgame()// after a previous game has finnished dont restart entire game but reinitilize certian parts
    {
        newGame = new ThirteenGameThread(Uitalk, gamemode, HumanPlayers);// creation of the gamethread
        playerHands = newGame.getGameState().getPlayerHands(); // initilize the game and get the player hands
        initCardImageViews();

        for (int i = 0; i < 13; i++) {
            shiftedUp.put(i, false);
        }

        for (int i = 0; i < 13; i++)//loop to set onclick listeners for all the cards in our hand
        {
            //getting the value of the card that maps to the resource id of the card image tied to the value
            cardImageViewsInHand.get(i).setBackgroundResource(cardMapping.get(playerHands.get(playernumber).get(i)));

            //TODO Handling multiplayer gamemode!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            if (gamemode == 2)//we are the client
            {
                cardImageViewsInHand.get(i).setVisibility(View.GONE);
            }
            //TODO End Handling multiplayer gamemode!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        }
        not_turn();// initially assume it is not our turn
        play.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        pass.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

        //TODO Handling multiplayer gamemode!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        if (gamemode == 0)//If we are not the client we start the game
        {
            updategameui();
            newGame.start();
        } else if (gamemode == 1) {
            init_Client();
            updategameui();
            newGame.start();
        }
        //TODO End Handling multiplayer gamemode!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


    }

    public void setHumanPlayers(int x) {
        HumanPlayers = x;
    }

    public void setPlayernumber(int x) {
        playernumber = x;
    }

    public void setGamemode(int x) {
        gamemode = x;
    }

    public void init_Client() {
        for (int x = 0; x < newGame.gameState.getPlayers().size(); x++) {
            if (newGame.gameState.getPlayers().get(x).isClienthuman()) {
                StringBuilder f = new StringBuilder();
                f.append("SG").append("-");
                for (int y = 0; y < newGame.gameState.getPlayerHands().get(x).size(); y++) {
                    if (y == newGame.gameState.getPlayerHands().get(x).size() - 1) {
                        f.append(newGame.gameState.getPlayerHands().get(x).get(y));
                    } else {
                        f.append(newGame.gameState.getPlayerHands().get(x).get(y)).append("-");
                    }
                }
                f.append("-").append("PN").append("-").append(x).append("\r\n");
                ClientConnectedIOThreads.get(x - 1).write(f.toString());

            }
        }
    }

    public void setClientConnectedIOThreads(ArrayList<BluetoothIOThread> h) {
        ClientConnectedIOThreads = h;
    }

    public void setHostConnectedIOThread(BluetoothIOThread h) {
        HostConnectedIOThread = h;
    }
}
