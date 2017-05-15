package com.example.mangaramu.thirteengamesmash;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * Created by mangaramu on 12/7/2016.
 */

public class HostGameFrag extends Fragment {

    ListView hostview;
    ArrayAdapter paireddevices;
    ArrayList<BluetoothDevice> bluedevicess= new ArrayList<BluetoothDevice>();
    ArrayList<BluetoothSocket> clients=new ArrayList<BluetoothSocket>();
    Button canncel;
    Button startgame;
    JoinGameFrag.joinhostbutt joinhost;
    BluetoothAdapter hostadapt;
    UUID thirUUID;
    View v;
    BroadcastReceiver bluerecieve;
    BluetoothHostConnectThread blueconn;
    ArrayList <BluetoothIOThread> blue= new ArrayList<BluetoothIOThread>();
    Boolean threadssent=false;
    Boolean connected=false;//checks if we were allowed to register the broadcast reciever

    private final int REQUEST_BT=1;
    private final int REQUEST_DCBT=2;
    public HostGameFrag()
    {
        //setRetainInstance(true);
    }

    Handler BlueScoketReturn = new Handler(){// handle what the thread for getting connections returns, put them in arraylist and then in the listadapter!
        @Override
        public void handleMessage(Message msg) {
            if(clients.size()<4) // maximum 3 clients!
            {
                if(!clients.contains((BluetoothSocket) msg.obj)) {
                    clients.add((BluetoothSocket) msg.obj);// add the Bluetooth socket obtained from the bluetooth thread to the arraylist.. then we should try to connect? when the play button is pressed?
                    bluedevicess.add(((BluetoothSocket) msg.obj).getRemoteDevice());//adds the conencted device to the arraylist of addrsses
                    paireddevices.add(((BluetoothSocket) msg.obj).getRemoteDevice().getName() + "\n" + ((BluetoothSocket) msg.obj).getRemoteDevice().getAddress());// adds the connected deveices address and name to the listview adapter
                }

            }
            else
            {
                blueconn.cancel();// stop the connection thread after we get 4 devices connected
            }
        }
    };

    @Override
    public void onAttach(Context context) {



        super.onAttach(context);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        try
        {
            joinhost=(JoinGameFrag.joinhostbutt)getActivity();
            paireddevices= new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1);
        }
        catch (ClassCastException e)
        {
            Log.d("Class Cast Exception","Attaching class must implement the joinhostbutt interface");
        }

        hostadapt=BluetoothAdapter.getDefaultAdapter();// getting the default bluetooth adapter
        thirUUID=UUID.fromString(getResources().getString(R.string.UUID));
        if(hostadapt==null && getActivity()!=null)// get activity can sometimes return a null on program exit!
        {
           Toast powtoast = Toast.makeText(getActivity(),"Device does not support Bluetooth",Toast.LENGTH_SHORT);
            powtoast.show();
        }
        else
        {
            if(!hostadapt.isEnabled())// check to see if the bluetooth adapter is enabled and if not.. we enable it
            {
                Intent BTenable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(BTenable,REQUEST_BT);// try to enable the bluetooth adapter
            }
            else
            {
                connected = true;
                paireddevices.add("Yourself");

                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);// Make an intent that tries to make the host discoverable
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 130);
                startActivityForResult(discoverableIntent, REQUEST_DCBT);
            /*Set<BluetoothDevice> pairedDevices = hostadapt.getBondedDevices();// checks to see if there are bonded devices
                if(pairedDevices.size()>0)
                {
                    for(BluetoothDevice device : pairedDevices)
                    {
                        paireddevices.add(device.getName()+"\n"+ device.getAddress());// adds the paired devices to the arrayadapter
                        blueaddresses.add(device.getAddress());// store the address
                    }
                }*/

            }
        }

        /*bluerecieve=new BroadcastReceiver() {// setting a bluetooth broadcast reciever that will do something on recieve from starting bluetooth discovery
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    paireddevices.add(device.getName() +"\n"+ device.getAddress());// add the new device to the listview
                    blueaddresses.add(device.getAddress());// store the address
                }
            }
        };*/ // may not need the broadcast reciever as we want to host not be a client

        IntentFilter filter= new IntentFilter(BluetoothDevice.ACTION_FOUND);//filters in
        getActivity().registerReceiver(bluerecieve,filter);// Don't forget to unregister during onDestroy


        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bluedevicess= new ArrayList<BluetoothDevice>();
        clients=new ArrayList<BluetoothSocket>();
        blue= new ArrayList<BluetoothIOThread>();

        v= inflater.inflate(R.layout.hostgamefrag,container,false);
        hostview=(ListView) v.findViewById(R.id.hostlist);
        startgame = (Button) v.findViewById(R.id.hoststart);
        canncel = (Button) v.findViewById(R.id.hostcancel);
         startgame.setOnClickListener(new View.OnClickListener() {//
             @Override
             public void onClick(View v) {
                 blueconn.cancel();
                 for(int f=0;f<clients.size();f++) {
                     blue.add(new BluetoothIOThread(clients.get(f),null));
                     blue.get(f).start();
                     String ty="SG\r\n";
                     blue.get(f).write(ty);
                     //android.os.SystemClock.sleep(1000);
                 }
                 joinhost.joinhost(0);// host starts the game
             }
         });
        canncel.setOnClickListener(new View.OnClickListener() {//
            @Override
            public void onClick(View v) {
                if(blue.size()>0)//for now we will cancel the blue threads on cancel button push!
                {
                    for (int o=0;o<blue.size();o++)
                    {
                        blue.get(o).cancel();
                    }

                }
                joinhost.joinhost(2);
            }
        });
        hostview.setAdapter(paireddevices);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {//for bluetooth
        if(requestCode==REQUEST_BT) {
            if (resultCode == getActivity().RESULT_OK)// if bluetooth was enabled we do other bluetooth stuff
            {
                connected = true;
                paireddevices.add("Yourself");

                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);// Make an intent that tries to make the host discoverable
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,130);
                startActivityForResult(discoverableIntent, REQUEST_DCBT);
            /*Set<BluetoothDevice> pairedDevices = hostadapt.getBondedDevices();// checks to see if there are bonded devices
                if(pairedDevices.size()>0)
                {
                    for(BluetoothDevice device : pairedDevices)
                    {
                        paireddevices.add(device.getName()+"\n"+ device.getAddress());// adds the paired devices to the arrayadapter
                        blueaddresses.add(device.getAddress());// store the address
                    }
                }*/
            }
            else if (getActivity()!=null)// get activity can sometimes return a null on program exit! and if bluetooth was not enabled toast that the bluetooth failed
            {
                connected = false;
                Toast powtoast = Toast.makeText(getActivity(), "Enabling Bluetooth failed, please try hosting again", Toast.LENGTH_SHORT);
                powtoast.show();
            }
        }
        else if(requestCode==REQUEST_DCBT) {
            if (!(resultCode == getActivity().RESULT_CANCELED))// if we were able to enable discoverability
            {
                blueconn = new BluetoothHostConnectThread(BlueScoketReturn, thirUUID, hostadapt, getResources().getString(R.string.app_name));
                blueconn.start();
                //should start the thread after to obtain connections .. at maximum only 4 connections! which is enforced at the handler
            } else if (getActivity()!=null)// get activity can sometimes return a null on program exit!
            {
                Toast powtoast = Toast.makeText(getActivity(), "Enabling Bluetooth discoverable failed, please try hosting again", Toast.LENGTH_SHORT);
                powtoast.show();
            }
        }
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
        // unregisters the reciever on onDestroy
//getActivity().unregisterReceiver(bluerecieve);

        super.onDestroy();
    }

    @Override
    public void onDetach() {
        if(!(bluerecieve==null)) {
            getActivity().unregisterReceiver(bluerecieve);
        }
        if(blueconn!=null && threadssent==true) {
            blueconn.Bbreak();
        }
        else if(blueconn!=null && threadssent==false)
        {
            blueconn.cancel();
        }
        if(blue.size()>0 && threadssent==false) {
            for (int o = 0; o < blue.size(); o++) // cancel the connection threads
            {
                blue.get(o).cancel();
            }
        }
        else if(blue.size()>0 && threadssent==true)
        {
            blue=null;// we stop refrencing those threads
        }
        if(clients!=null&& threadssent==false)
        {
            for (int o = 0; o < clients.size(); o++) // cancel the connection threads
            {
                try {
                    clients.get(o).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            clients.clear();
        }
        else if(clients!=null&& threadssent==true)
        {
            clients.clear();
        }

        super.onDetach();
    }
    public ArrayList getClinets()// returns the clients arraylist
    {
        return clients;
    }
    public ArrayList getDevices()// returns the clients arraylist
    {
        return bluedevicess;
    }
    public ArrayList getIOThreads() {
        threadssent=true;
        return blue;//returns the iothreads host uses to communicate to the clients
    }
}
