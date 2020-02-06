package com.relay;

import com.relay.rmi.RelayLauncher;
import com.relay.rmi.RelayLauncherImpl;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;


// Server starts begins listening, and spawns follower. Leader server sends ping to follower letting it know it is alive.
// Leader server notifies follower of all open connections so they can be reconnected by follower in case of failure.
// If follower times out before receiving a ping, it takes over and starts listening, and spawns a follower.
public class Main {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        String host = "localhost";
        boolean isFollower = false;
        final int clonePort = 1900;

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
                if (args[i].equalsIgnoreCase("-f")){
                    i++;;
                    isFollower = Boolean.parseBoolean(args[i]);
                }
            }

        }

        RelayLauncher relayLauncher = new RelayLauncherImpl();
        if(isFollower){
            System.out.println("Launching follower");
            System.setProperty("java.rmi.server.hostname",host);
            LocateRegistry.createRegistry(clonePort);
            Naming.rebind("rmi://"+host+":"+clonePort+"/relay", relayLauncher);
            relayLauncher.launchFollower(host, port);
        } else{
            System.out.println("Launching Leader");
            relayLauncher.launchLeader(host, port);
        }
    }
}
