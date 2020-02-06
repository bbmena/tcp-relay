package com.relay;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ProxyAndRelayHandler implements Runnable  {

    private Socket relayedClientSocket;
    private ServerSocket proxiedClientDataServer;

    public ProxyAndRelayHandler(Socket relayedClientSocket, ServerSocket proxiedClientDataServer){
        this.relayedClientSocket = relayedClientSocket;
        this.proxiedClientDataServer = proxiedClientDataServer;
    }

    public void run() {

        try {
            Socket proxiedClientDataSocket = proxiedClientDataServer.accept();
            new Thread(new RelayConnectionHandler(proxiedClientDataSocket, relayedClientSocket)).start();
            new Thread(new RelayConnectionHandler(relayedClientSocket, proxiedClientDataSocket)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
