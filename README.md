### TCP Relay Server

This is a generic server that can be used to transmit data to and from a server that it is not
possible to connect with directly.

##### Useage instructions

Build to jar and run: `java -jar tcp-relay.jar -p <Port> -h <Host>` All args optional

The relay server should be initialized with arguments defining a host and port. If none are defined,
the default is localhost:8080

In order to work with the relay, your server should be able to open a socket connection at the host and port outlined above.

Upon connection to the relay, a public host and port will be sent to your server in string format: `HOST:PORT`. This is the
address that can be used to connect to you. As new connections are made, port and host combinations will be sent that you can 
connect to to communicate with connected clients.


Example:


    Socket relayListenerSocket = new Socket(host, relayPort);
    System.out.println("Connected to relay at " + host + " on port " + relayPort);
    BufferedReader relayIn = new BufferedReader(new InputStreamReader(relayListenerSocket.getInputStream()));
    System.out.println("Contact me at: " + relayIn.readLine());

    while(true) {
        String line;
        while((line = relayIn.readLine())!= null) {
            String[] hostAndPort =  line.split(":");
            Socket newListenerSocket = new Socket(hostAndPort[0], Integer.parseInt(hostAndPort[1]));
            new Thread(new ConnectionHandler(newListenerSocket)).start();
        }
    }
