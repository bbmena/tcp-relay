package com.crowdstorage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RelayConnectionHandler implements Runnable {
    private static final int BUFFER_SIZE = 8192;
    private Socket sender;
    private Socket receiver;

    public RelayConnectionHandler(Socket sender, Socket receiver){
        this.sender = sender;
        this.receiver = receiver;
    }

    public void run(){
        char[] message = new char[BUFFER_SIZE];
        BufferedReader readFromSender;
        PrintWriter writeToReceiver = null;

        try {
            readFromSender = new BufferedReader(new InputStreamReader(sender.getInputStream()));
            writeToReceiver = new PrintWriter(receiver.getOutputStream());
            int val;
            while((val = readFromSender.read(message)) != -1) {
                writeToReceiver.write(message, 0, val);
                writeToReceiver.flush();
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            writeToReceiver.flush();
        }

    }
}
