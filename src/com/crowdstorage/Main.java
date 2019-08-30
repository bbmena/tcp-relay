package com.crowdstorage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        String host = "localhost";

        if(args.length == 0){
            System.out.println("No port or host specified. Starting relay server at localhost on port 8080");
        } else {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equalsIgnoreCase("-p")) {
                    i++;
                    port = Integer.parseInt(args[i]);
                }
                if (args[i].equalsIgnoreCase("-h")) {
                    i++;
                    host = args[i];
                }
            }
            System.out.println("Starting relay server on " + host + ":" + port);
        }

        ServerSocket socketServer = new ServerSocket(port);

        while(true) {
            Socket proxiedClientSocket = socketServer.accept();
            new Thread(new RelayerSocketConnection(proxiedClientSocket, host)).start();
        }
    }
}
