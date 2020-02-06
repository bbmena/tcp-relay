package com.relay.rmi;

import java.net.MalformedURLException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RelayLauncher extends Remote {

    public boolean ping() throws RemoteException;

    public void launchLeader(String host, int port)  throws RemoteException;

    public void launchFollower(String host, int port) throws RemoteException, MalformedURLException;
}
