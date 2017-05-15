package com.example.mangaramu.thirteengamesmash;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

/**
 * Created by mangaramu on 12/5/2016.
 */

public class GameOverFrag extends Fragment{
    GameOverFrag.gameover gameover;
    View v;
    Button yes, no;
    ArrayAdapter t;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try
        {
            gameover=(GameOverFrag.gameover)getActivity();
        }
        catch(ClassCastException e)
        {
            Log.d("Class Exception", "Attaching class must implemtne the gameover interface");
        }
        v=inflater.inflate(R.layout.playagainfrag,container,false);
        yes= (Button)v.findViewById(R.id.gameoverno);
        no= (Button)v.findViewById(R.id.gameoveryes);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameover.gameover(1);
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameover.gameover(2);
            }
        });

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
    }

    public interface gameover// interface for handling menu selects
    {
        void gameover(int f);
    }
}
