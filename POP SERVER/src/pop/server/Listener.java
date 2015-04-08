package pop.server;

import java.io.*;

import java.net.*;

public class Listener extends Thread{
    String receivedM = "";
    private MessageLogger logger;
    private RemoteDevice remoteDevice;
    private BufferedReader br;

    public Listener(RemoteDevice aClient, MessageLogger aLogger) throws IOException {
        remoteDevice = aClient;
        logger = aLogger;
        Socket socket = remoteDevice.socket;
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public String getMessage() {
        return receivedM;
    }

    /**     
     * While not interrupted, messages are read from the client socket, messages are then forwarded    
     * to the MessageLogger's message Vector (queue). Then the MessageLogger is notified.     
     */
    public void run() {
        try {
            while (!isInterrupted()) {
                String message = br.readLine();
                //messageM += message + "\r\n";
                receivedM = message;
                if (message == null) {
                    break;
                }
                logger.postMessage(remoteDevice, message);
            }
        } catch (IOException ioex) {
            // could not read from socket
        }

        // Broken. Interrupt both listener and sender threads.
        remoteDevice.sender.interrupt();
        logger.deleteRemoteDevice(remoteDevice);

    }

}
