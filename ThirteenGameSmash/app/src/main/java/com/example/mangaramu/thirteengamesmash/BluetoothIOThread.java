package com.example.mangaramu.thirteengamesmash;

/**
 * Created by mangaramu on 12/7/2016.
 */

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class BluetoothIOThread extends Thread {
    // final BluetoothServerSocket;
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private Handler[] twopok;
    private Handler Clientstarthandle;
    private BufferedInputStream tmpbuffIn;
    private BufferedOutputStream tmpbuffout;
    private BufferedReader buffinread;
    private BufferedWriter buffoutwrite;
    private InputStreamReader inreadl;
    private OutputStreamWriter outwrite;
    private String tmpstring;
    private Boolean end = false;


    public BluetoothIOThread(BluetoothSocket socket, Handler u) {// default constructor for the multiplayer thread object.....
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        BufferedOutputStream tmpbuout = null;
        BufferedInputStream tmpbuin = null;
        Clientstarthandle = u;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
        tmpbuffIn = new BufferedInputStream(mmInStream);
        try {
            inreadl = new InputStreamReader(tmpbuffIn, "UTF-8"); // the values we read will be encoded in Utf-8
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        buffinread = new BufferedReader(inreadl);//use a buffered input stream reader to read data

        tmpbuffout = new BufferedOutputStream(mmOutStream);
        try {
            outwrite = new OutputStreamWriter(tmpbuffout, "UTF-8");// the values we send will be encoded in Utf-8
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        buffoutwrite = new BufferedWriter(outwrite);//use a buffered output stream writer to send data


    }

    public void run() { // should handle the diffrent things we can send inthis! then send messages to diffrent handlsrs
        //// depending on what we get sent (if this is a client getting stuff from host)
        byte[] buffer = new byte[1024];  // buffer store for the stream, there may be a reason why the number is 1024 for the buffer... gotta keep that in mind
        int bytes; // bytes returned from read()


        /*Command convention:This thread will be able to obtain inputs, and from those inputs call different handlers
        * Write Conventions:
        * Cards= (number)-(number)-(number)-(number)(nothing) or (number)-(number)-(number)-(some other identifier for something else)
        * Start Turn : ST
        * End Turn: ET-the Cards played
        * Set Player Number:SPN-Playernumber
        * UpdateGame:UG-(playernumber)-PP-PlayPile (host will know the player number)
        * StartGame:SG-(player Cards)-PN-(player number)
        * EndGame:EG*/

        // Keep listening to the InputStream until an exception occurs
        while (!end) {
            try {
                // Read from the InputStream
                tmpstring = buffinread.readLine();
                if (twopok == null) {// if the handler is not there
                    // bytes = mmInStream.read(buffer);
                    if (tmpstring.equals("SG") && Clientstarthandle != null) //OHH MY GOD... DID I JUST DO THAT!?!?!?!
                    {
                        Message p = Message.obtain();
                        p.obj = tmpstring;
                        p.setTarget(Clientstarthandle);
                        p.sendToTarget();
                    }
                } else//{gameuihandle,turnstart,gameover,setplayernumb,setplayerHand}; // Uitalk object
                {
                    String[] temp;
                    ArrayList<Integer> hand = new ArrayList<Integer>();
                    ArrayList<Integer> played = new ArrayList<Integer>();
                    temp = tmpstring.split("-");
                    if (temp[0].equals("SG"))// if we are starting clientgame (sent from host to client)
                    {
                        int i;
                        for (i = 1; i < temp.length; i++) {
                            try {
                                hand.add(Integer.valueOf(temp[i]));
                            } catch (NumberFormatException e) {
                                break;
                            }
                        }

                        Message p = Message.obtain();
                        p.obj = Integer.valueOf(temp[i + 1]);//integer of the playernumber assigned to the hand
                        p.setTarget(twopok[3]);//send playernumber to the playernumber handler
                        p.sendToTarget();

                        /* TODO should confirm that client recieved and successfully completed the assigned task linked to the message
                        before sending another one.... */
                        android.os.SystemClock.sleep(1000);

                        p = Message.obtain();
                        p.obj = hand;
                        p.setTarget(twopok[4]);//send hand to the playerhand handler
                        p.sendToTarget();

                        //android.os.SystemClock.sleep(1000);

                        /*Message a = Message.obtain();
                        p.obj="";
                        p.setTarget(twopok[0]);//notify to start the gameuihandler
                        p.sendToTarget();*/

                       /* Message d = Message.obtain();
                        p.obj="";
                        p.setTarget(twopok[0]);//notify again to start the gameuihandler
                        p.sendToTarget();
                        hand.clear();*/

                    } else if (temp[0].equals("EG"))// if we are ending the game (sent from host to client)
                    {
                        Message p = Message.obtain();
                        p.obj = "";
                        p.setTarget(twopok[2]);
                        p.sendToTarget();
                    } else if (temp[0].equals("ST"))// if we are starting the turn (sent from host to client)
                    {
                        Message p = Message.obtain();
                        p.obj = "";
                        p.setTarget(twopok[1]);// start the client's turn!
                        p.sendToTarget();
                    } else if (temp[0].equals("ET"))// if we are ending the turn (sent from client to host)
                    {
                        int i;
                        for (i = 1; i < temp.length; i++) {
                            try {
                                played.add(Integer.valueOf(temp[i]));
                            } catch (NumberFormatException e) {
                                break;
                            }
                        }
                        Message p = Message.obtain();
                        p.obj = played;
                        p.setTarget(twopok[6]);// end of clients turn!
                        p.sendToTarget();

                    } else if (temp[0].equals("UG"))// if we are just getting a turn update from host (sent from host to client)
                    {
                        int g;
                        g = Integer.valueOf(temp[1]);

                        if(temp.length>3) {
                            if (temp[3].equals("NR")) {
                                played = null;
                            } else {
                                for (int i = 3; i < temp.length; i++) {
                                    played.add(Integer.valueOf(temp[i]));
                                }
                            }
                        }

                        GameTurn f = new GameTurn(g, played);
                        Message p = Message.obtain();
                        p.obj = f;
                        p.setTarget(twopok[0]);// end of clients turn!
                        p.sendToTarget();


                    }

                }

                // Send the obtained bytes to the UI activity
                //mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                //.sendToTarget();
            } catch (IOException e) {
                android.os.SystemClock.sleep(300);
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(String h) {
        try {
            buffoutwrite.write(h);// use a buffered writer to write to a device
            buffoutwrite.flush();// flush the buffer to push the message
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        end = true;
        try {
            mmSocket.close();
        } catch (IOException e) {
        }
    }

    public void Bbreak() {
        end = true;
    }

    public void setHandlerArr(Handler[] j) {
        twopok = j;
    }
}
