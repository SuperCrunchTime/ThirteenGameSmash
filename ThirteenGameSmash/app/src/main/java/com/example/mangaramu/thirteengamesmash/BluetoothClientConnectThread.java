package com.example.mangaramu.thirteengamesmash;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by mangaramu on 12/7/2016.
 */

public class BluetoothClientConnectThread extends Thread {
    UUID connectionid;
    Handler Fragsendconnect;// should send back the connection we obtain to this handler...
    BluetoothDevice hostdevice;
    BluetoothSocket connection;
    BluetoothAdapter clientadapter;

    String name;

    BluetoothClientConnectThread(Handler x, UUID y,BluetoothDevice r,BluetoothAdapter f)
    {
        Fragsendconnect=x;
        connectionid=y;
        hostdevice=r;
        clientadapter=f;
        // Use a temporary object that is later assigned to connections,
        // because mmServerSocket is final
        BluetoothSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = hostdevice.createInsecureRfcommSocketToServiceRecord(connectionid);
        } catch (IOException e) { }
        connection = tmp;
    }
    @Override
    public void run() {
       clientadapter.cancelDiscovery();//stops the doscovery because now we are trying to connect

        try//try to connect and if there are no problems, send the connection to the joingame thread
        {
            connection.connect();
            Message f= Message.obtain();
            f.obj=connection;
            f.setTarget(Fragsendconnect);
            f.sendToTarget();
        }
        catch (IOException e)
        {
            Message f= Message.obtain();
            f.obj="";// connection failed!
            f.setTarget(Fragsendconnect);
            f.sendToTarget();
            try
            {
                connection.close();
            }
            catch(IOException a)
            {
                return;
            }
        }
    }

    public void Cancel()
    {
        try {
            connection.close();
        } catch (IOException e) { }

    }
    public void derefrence()
    {
        Fragsendconnect=null;// should send back the connection we obtain to this handler...
        hostdevice=null;
        connection=null;
        clientadapter=null;
    }
}
