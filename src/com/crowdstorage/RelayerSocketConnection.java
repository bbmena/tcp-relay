package com.crowdstorage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class RelayerSocketConnection implements Runnable {
    private Socket proxiedClientNotifierSocket;
    private String host;

    public RelayerSocketConnection(Socket proxiedClientSocket, String host){
        this.proxiedClientNotifierSocket = proxiedClientSocket;
        this.host = host;
    }

    public void run() {
        ServerSocket relayedServerSocket = null;

        try {
            PrintWriter writeToProxy = new PrintWriter(proxiedClientNotifierSocket.getOutputStream());
            relayedServerSocket = new ServerSocket(0);
            sendConnectionMessage(writeToProxy, relayedServerSocket.getLocalPort());
            while(true) {
                Socket relayedClientSocket = relayedServerSocket.accept();
                new Thread(new ProxyAndRelayHandler(relayedClientSocket, proxiedClientNotifierSocket, host)).start();
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

    private void sendConnectionMessage(PrintWriter writeToProxy, int port){
        System.out.println("established relay address: " + host + ":" + port);
        writeToProxy.println(host +":"+port);
        writeToProxy.flush();
    }
}
