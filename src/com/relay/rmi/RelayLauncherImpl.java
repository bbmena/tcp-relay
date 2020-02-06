package com.relay.rmi;

import com.relay.Main;
import com.relay.RelayerSocketConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RelayLauncherImpl extends UnicastRemoteObject implements RelayLauncher {
    private volatile long timestamp;
    private final long timeToWait = 350L;
    private final int clonePort = 1900;

    public RelayLauncherImpl() throws RemoteException {
        super();
        this.timestamp = System.currentTimeMillis();
    }

    public void launchLeader(String host, int port) throws RemoteException {
        Thread thread = new Thread(() -> {
            System.out.println("Ping service running");
            try{
                startNewRelay(host, port);
                Thread.sleep(500);
                RelayLauncher relayLauncher = (RelayLauncher)Naming.lookup("rmi://"+host+":"+clonePort+"/relay");
                boolean p = relayLauncher.ping();
                System.out.println(p);
                while(p){
                    Thread.sleep(timeToWait-150L);
                    p = relayLauncher.ping();
                    if(!p){
                        startNewRelay(host, port);
                        Thread.sleep(500);
                        relayLauncher = (RelayLauncher)Naming.lookup("rmi://"+host+":"+clonePort+"/relay");
                        p = relayLauncher.ping();
                    }
                }
            } catch (RemoteException | MalformedURLException | NotBoundException | InterruptedException e){
                e.printStackTrace();
            }
        });
        thread.start();
        startRelayServer(host, port);
    }

    public void launchFollower(String host, int port) throws RemoteException {
        System.out.println("Follower Launched");
        this.timestamp = System.currentTimeMillis();
        boolean isFollower = true;

        // Give the server time to spin everything up and send its first ping
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while(isFollower){
            if(System.currentTimeMillis() - this.timestamp > timeToWait){
                isFollower = false;
            } else {
                try{
                    Thread.sleep(timeToWait);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        try{
            System.out.println("Look at me. I'm the captain now.");
            launchLeader(host, port);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean ping() throws RemoteException{
        this.timestamp = System.currentTimeMillis();
        return true;
    }

    public void startRelayServer(String host, int port) {
        try{
            System.out.println("Starting relay server on " + host + ":" + port);
            ServerSocket socketServer = new ServerSocket(port);

            while(true) {
                Socket proxiedClientSocket = socketServer.accept();
                new Thread(new RelayerSocketConnection(proxiedClientSocket, host)).start();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void startNewRelay(String host, int port){
        try{
            String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            System.out.println("Launching new process at " + path);
            Process process = new ProcessBuilder("java", "-jar", path, "-p", String.valueOf(port), "-h", host, "-f", "true").start();

            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));

            Thread thread = new Thread(() -> {
                String s;
                try{
                    while((s = in.readLine()) != null){
                        System.out.println(s);
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            });
            thread.start();

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
