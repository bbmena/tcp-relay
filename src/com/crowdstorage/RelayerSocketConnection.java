package com.crowdstorage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RelayerSocketConnection implements Runnable {
    private Socket proxiedClientSocket;
    private String host;

    public RelayerSocketConnection(Socket proxiedClientSocket, String host){
        this.proxiedClientSocket = proxiedClientSocket;
        this.host = host;
    }

    public void run() {
        ServerSocket relayedServerSocket = null;

        try {
            relayedServerSocket = new ServerSocket(0);
            System.out.println("established relay address: " + host + ":" + relayedServerSocket.getLocalPort());
            while(true) {
                Socket relayedClientSocket = relayedServerSocket.accept();
                new Thread(new RelayConnectionHandler(proxiedClientSocket, relayedClientSocket)).start();
                new Thread(new RelayConnectionHandler(relayedClientSocket, proxiedClientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                relayedServerSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
