package com.example.mangaramu.thirteengamesmash;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;


public class ThirteenGameProj extends Activity implements TitleScreen.buttonselect,menufrag.quitmenusselect, GameUI.gamebutton,GameOverFrag.gameover,JoinGameFrag.joinhostbutt{
    FragmentManager fm;
    FragmentTransaction ft;
    GameUI thirgame = new GameUI();
    TitleScreen thirmenu = new TitleScreen();
    menufrag menfr=new menufrag();
    GameOverFrag overfrag=new GameOverFrag();
    HostGameFrag host=new HostGameFrag();
    JoinGameFrag join= new JoinGameFrag();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fm=getFragmentManager();
        fm.beginTransaction().add(R.id.gamehold,thirmenu).commit();
        fm.executePendingTransactions();

    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void singlemultibutton(int x) { //takes in an int passed by a the titlescreen fragment then changes fragement to what that value corrisponds to
        switch (x){ // to see which button was pressed from the title menu
            case 0://singleplayer
                thirgame.gamemode=0;
                fm.beginTransaction().replace(R.id.gamehold,thirgame).commit();
                fm.executePendingTransactions();
                break;
            case 1:// host multiplayer

                fm.beginTransaction().replace(R.id.gamehold,host).commit();
                fm.executePendingTransactions();//TODO make ui for a lobby style system that shows names of connections including you and the host
                break;
            case 2:// join multiplayer

                fm.beginTransaction().replace(R.id.gamehold,join).commit();
                fm.executePendingTransactions(); //TODO make ui for a lobby style system that shows names of connections including you and the host
                break;
            case 3://AI play
                thirgame.gamemode=0;
                thirgame.setHumanPlayers(0);
                fm.beginTransaction().replace(R.id.gamehold,thirgame).commit();
                fm.executePendingTransactions();
                break;
            default:
                break;
        }

    }

    @Override
    public void quitmenu(int f) { //takes in an int passed by a the game fragment then changes fragement to what that value corrisponds to
        switch (f){
            case 0:

                fm.beginTransaction().replace(R.id.gamehold,thirmenu).remove(menfr).commit();
                fm.executePendingTransactions();
                thirgame=new GameUI();//reinitilize this fragment
                break;
            case 1:
                fm.beginTransaction().remove(menfr).commit();
                fm.executePendingTransactions();
                break;
            default:
                break;
        }

    }

    @Override
    public void gamebuttonclick(int x) {
        switch (x){ //takes in an int passed by a the menufrag game menu fragment then changes fragement to what that value corrisponds to
            case 0:
                fm.beginTransaction().add(android.R.id.content,overfrag).commit();
                fm.executePendingTransactions();
                break;
           case 2:

                fm.beginTransaction().add(android.R.id.content, menfr).commit();
                fm.executePendingTransactions();

            default:
                break;
        }

    }

    @Override
    public void gameover(int f) {//takes in an int passed by a the gameui fragment then does an action corresponding to the int given
        switch (f) {
            case 1:
                fm.beginTransaction().remove(overfrag).commit();
                fm.executePendingTransactions();
                thirgame.newnewgame();
                break;
            case 2:
                fm.beginTransaction().remove(overfrag).remove(thirgame).replace(R.id.gamehold, thirmenu).commit();
                fm.executePendingTransactions();
                thirgame=new GameUI();//reinitilize this fragment
                break;
            default:
                break;
        }
    }
    @Override
    public void joinhost(int d) {//takes in an int passed by a the gameui fragment then does an action corresponding to the int given
        switch(d)
        {
            case 0://start a gameui gametype hostgame and pass some arguments!
                thirgame.setClientConnectedIOThreads(host.getIOThreads());// giving the bluetooth scoket created by the joinGameFrag into the gam
                thirgame.setGamemode(1);
                fm.beginTransaction().replace(R.id.gamehold,thirgame).commit();
                fm.executePendingTransactions();
                host=new HostGameFrag();//reinitilize this fragment
                break;
            case 1://TODO handle the joining of the game stuff :O start a gamui gametype client and pass some arguemnts
                thirgame.setHostConnectedIOThread(join.getClientThread());// giving the bluetooth scoket created by the joinGameFrag into the gam
                thirgame.setGamemode(2);
                fm.beginTransaction().replace(R.id.gamehold,thirgame).commit();//TODO we need to pass in some extra information as well! hostmode and clientmode
                fm.executePendingTransactions();
                join=new JoinGameFrag();//reinitilize this fragment
                break;
            case 2:
                fm.beginTransaction().replace(R.id.gamehold,thirmenu).commit();
                fm.executePendingTransactions();
                break;
            default:
            break;
        }

    }

    @Override
    public void onBackPressed() {
        if(fm.findFragmentById(android.R.id.content) instanceof menufrag)// check if our fragment within android.R.id.content is an instanceof menufrag so we can change back button behavior
        {
            fm.beginTransaction().remove(menfr).commit();
            fm.executePendingTransactions();
        }
        else if(fm.findFragmentById(R.id.gamehold) instanceof GameUI)
        {
            fm.beginTransaction().add(android.R.id.content, menfr).commit();
            fm.executePendingTransactions();
        }
        else if (fm.findFragmentById(R.id.gamehold) instanceof GameOverFrag)
        {

        }
        else if (fm.findFragmentById(R.id.gamehold) instanceof HostGameFrag || fm.findFragmentById(R.id.gamehold) instanceof JoinGameFrag)
        {

        }
        else {
            super.onBackPressed();
        }

    }



}
