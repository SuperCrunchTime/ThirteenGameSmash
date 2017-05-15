package com.example.mangaramu.thirteengamesmash;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * Created by mangaramu on 12/7/2016.
 */

public class JoinGameFrag extends Fragment {
    joinhostbutt joinhost;
    ArrayAdapter paireddevices;
    ArrayList<BluetoothDevice> devices= new ArrayList<BluetoothDevice>();
    Set<BluetoothDevice> pairedDev;
    BluetoothAdapter joinadapt;
    BluetoothDevice device;
    ListView joinview;
    Button canncel;
    Button join;
    UUID thirUUID;
    View v;
    BluetoothSocket hostconnection;
    BluetoothClientConnectThread clientconnection;
    BluetoothIOThread io;
    Boolean threadsent=false;
    private final int REQUEST_BT=1;

    public JoinGameFrag() {
        //setRetainInstance(true);
    }

    Handler BlueScoketReturn = new Handler(){
        @Override
        public void handleMessage(Message msg) {// what returns should be a BluetoothSocket... that is connected.
            // maybe toast that the connection succeeded/failed
            joinadapt.cancelDiscovery();// stop discovery if we have obtained a connection
            if(msg.obj.equals("")&& getActivity()!=null)// get activity can sometimes return a null on program exit!
            {
                Toast powtoast =Toast.makeText(getActivity(),"Connection to host failed",Toast.LENGTH_SHORT);
                powtoast.show();
            }
            else if(getActivity()!=null)// get activity can sometimes return a null on program exit!
            {
            hostconnection= (BluetoothSocket) msg.obj;
            Toast powtoast =Toast.makeText(getActivity(),"Connection Sucessful, the host should see your device on their screen",Toast.LENGTH_LONG);
            powtoast.show();
            io = new BluetoothIOThread(hostconnection,BluetoothStartgame);// TODO maybe to facilitate changing to the game from the host's bluetoothconnection
            io.start();
            }
        }
    };

    Handler BluetoothStartgame= new Handler(){// handler that when a message is recieved... starts the clients game
        @Override
        public void handleMessage(Message msg) {
            joinhost.joinhost(1);
        }
    };

    BroadcastReceiver bluerecieve=new BroadcastReceiver() {// setting a bluetooth broadcast reciever that will do something on recieve from starting bluetooth discovery
        @Override
        public void onReceive(Context context, Intent intent) {// to recieve the bluetooth device discoveries
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action))
            {
                 device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(!devices.contains(device)) {
                    paireddevices.add(device.getName() + "\n" + device.getAddress());// add the new device to the arrayadapter
                    devices.add(device);// add the the device to a arraylist (the refrence of the device)
                }
                else
                {

                }
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
         devices= new ArrayList<BluetoothDevice>();

        try {
            joinhost = (joinhostbutt) getActivity();
        }
        catch (ClassCastException e)
        {
            Log.d("Class Cast Exception","Attaching class must implement the joinhostbutt interface");
        }

        joinadapt=BluetoothAdapter.getDefaultAdapter();// getting the defualt bluetooth adapter
        joinadapt.cancelDiscovery();
        thirUUID= UUID.fromString(getResources().getString(R.string.UUID));
        paireddevices=new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1);



        if(joinadapt==null&& getActivity()!=null)// get activity can sometimes return a null on program exit!
        {
            Toast powtoast = Toast.makeText(getActivity(),"Device does not support Bluetooth",Toast.LENGTH_SHORT);
            powtoast.show();
        }
        else
        {
            if(!joinadapt.isEnabled())// check to see if the bluetooth adapter is enabled and if not.. we enable it
            {
                Intent BTenable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(BTenable,REQUEST_BT);// try to enable th ebluetooth adapter
            }
            else
            {
                pairedDev = joinadapt.getBondedDevices();// checks to see if there are bonded devices
                if(pairedDev.size()>0)
                {
                    for(BluetoothDevice device : pairedDev)
                    {
                        if(!devices.contains(device)) {
                            paireddevices.add(device.getName() + "\n" + device.getAddress());// add the new device to the arrayadapter
                            devices.add(device);// add the the device to a arraylist
                        }
                        else
                        {

                        }
                    }
                }
                joinadapt.cancelDiscovery();
                if(joinadapt.startDiscovery())
                {

                }
                else if (getActivity()!=null)// get activity can sometimes return a null on program exit!
              {
                  Toast powtoast = Toast.makeText(getActivity(),"Device discovery could not be started please try again",Toast.LENGTH_SHORT);
                  powtoast.show();
              }
            }


        }
        IntentFilter filter= new IntentFilter(BluetoothDevice.ACTION_FOUND);//filters in
        getActivity().registerReceiver(bluerecieve,filter);

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.joingamefrag, container, false);
        joinview = (ListView) v.findViewById(R.id.joinlist);
        canncel = (Button) v.findViewById(R.id.joincancel);

        canncel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            joinhost.joinhost(2);
            }
        });
        joinview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("the positoin number is","THHHHHHHHHHHHHHHHHE POSITION "+Integer.toString(position));
                    if(clientconnection !=null)// cancel a client connection if we have one
                    {
                        clientconnection.Cancel();
                    }
                    joinadapt.cancelDiscovery();
                    clientconnection = new BluetoothClientConnectThread(BlueScoketReturn, thirUUID, devices.get(position), joinadapt);// try to connect to the address that we click on
                    clientconnection.start();

            }
        });
        joinview.setAdapter(paireddevices);


        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {//for bluetooth
        if(requestCode==REQUEST_BT && resultCode==getActivity().RESULT_OK)// if bluetooth was enabled we do other bluetooth stuff
        {
            pairedDev = joinadapt.getBondedDevices();// checks to see if there are bonded devices
            if(pairedDev.size()>0)
            {
                for(BluetoothDevice device : pairedDev)
                {
                    if(!devices.contains(device)) {
                        paireddevices.add(device.getName() + "\n" + device.getAddress());// add the new device to the arrayadapter
                        devices.add(device);// add the the device to a arraylist
                    }
                    else
                    {

                    }
                }
            }
            joinadapt.cancelDiscovery();
            if(joinadapt.startDiscovery())
            {

            }
            else if(getActivity()!=null)// get activity can sometimes return a null on program exit!
            {
                Toast powtoast = Toast.makeText(getActivity(),"Device discovery could not be started please try again",Toast.LENGTH_SHORT);
                powtoast.show();
            }
        }
        else if(getActivity()!=null)// if bluetooth was not enabled toast that the bluetooth failed
        {
           Toast powtoast= Toast.makeText(getActivity(),"Enabling Bluetooth failed, please try joining again",Toast.LENGTH_SHORT);
            powtoast.show();
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

        super.onDestroy();
    }

    @Override
    public void onDetach() {
        if(!(bluerecieve==null))
        {
            getActivity().unregisterReceiver(bluerecieve);// unregisters the reciever on onDestroy
        }
        if(clientconnection!=null && threadsent==false)
        {
            clientconnection.Cancel();
        }
        if(clientconnection!=null && threadsent==true)
        {
            clientconnection.derefrence();
        }
        if(io!=null && threadsent==false)
        {
            io.cancel();
        }
        else if(io!=null && threadsent==true)
        {
            io=null;// we stop refrencing those threads
        }
        joinadapt.cancelDiscovery();
        if(hostconnection!=null &&threadsent==false)
        {
            try {
                hostconnection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            hostconnection=null;
        }
        else if(hostconnection!=null &&threadsent==true)
        {
            hostconnection=null;
        }
        super.onDetach();
    }

    public interface joinhostbutt {
        void joinhost(int d);
    }

    public BluetoothSocket getBluetoothSocket ()// returns the bluetooth socket obtained
    {
        return hostconnection;
    }
    public BluetoothSocket getBluetoothDevice ()// returns the bluetooth socket obtained
    {
        return hostconnection;
    }
    public BluetoothIOThread getClientThread()
    {
        threadsent=true;
        return io;

    }
}
