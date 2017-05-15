package com.example.mangaramu.thirteengamesmash;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.util.UUID;

import static android.provider.Telephony.Carriers.NAME;

/**
 * Created by mangaramu on 12/7/2016.
 */

public class BluetoothHostConnectThread extends Thread {
    BluetoothServerSocket connections;
    UUID connectionid;
    Handler Fragsendconnect;// should send back the connections we obtain to this handler... and only a maximum of 4
    BluetoothAdapter hostadapt;
    BluetoothSocket connection;
    String name;
    Boolean qquit=false;

    BluetoothHostConnectThread(Handler x, UUID y, BluetoothAdapter r,String appname)
    {
        name=appname;
        Fragsendconnect=x;
        connectionid=y;
        hostadapt=r;
        // Use a temporary object that is later assigned to connections,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = hostadapt.listenUsingRfcommWithServiceRecord(name, connectionid);
        } catch (IOException e) { }
        connections = tmp;
    }

    @Override
    public void run() {
        connection=null;
        while(!qquit)
        {
            try
            {
                connection =connections.accept();//try to accept a connection from someon who want to connect .. when we accept the connection the connection is already connected! do not need to call connect on it!
            }
            catch(IOException e)
            {
                break;
            }
            Message bluemessage =Message.obtain();// sends the connection that was obtianed back to the joingamefrag
            bluemessage.obj=connection;
            bluemessage.setTarget(Fragsendconnect);
            bluemessage.sendToTarget();

        }
    }

    public void cancel()
    {
       try
        {
            connections.close();// try to close the server scocket.
        }
        catch(IOException e)
        {

        }
    }
    public void Bbreak()
    {
        qquit=true;
        Fragsendconnect= null;// should send back the connections we obtain to this handler... and only a maximum of 4
        hostadapt= null;;
        connection = null;;
    }
}
