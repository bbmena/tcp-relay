package com.crowdstorage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ProxyAndRelayHandler implements Runnable  {

    private Socket relayedClientSocket;
    private Socket proxiedClientNotifierSocket;
    private String host;

    public ProxyAndRelayHandler(Socket relayedClientSocket, Socket proxiedClientNotifierSocket, String host){
        this.relayedClientSocket = relayedClientSocket;
        this.proxiedClientNotifierSocket = proxiedClientNotifierSocket;
        this.host = host;
    }

    public void run() {
        ServerSocket proxiedClientDataServer;

        try {
            PrintWriter writeToProxy = new PrintWriter(proxiedClientNotifierSocket.getOutputStream());
            proxiedClientDataServer = new ServerSocket(0);
            writeToProxy.println(host + ":" + proxiedClientDataServer.getLocalPort());
            writeToProxy.flush();
            Socket proxiedClientDataSocket = proxiedClientDataServer.accept();
            new Thread(new RelayConnectionHandler(proxiedClientDataSocket, relayedClientSocket)).start();
            new Thread(new RelayConnectionHandler(relayedClientSocket, proxiedClientDataSocket)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
