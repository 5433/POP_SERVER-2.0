package pop.server;

import java.net.*;
import java.util.*; 

public class MessageLogger extends Thread

{
    public volatile String castMessage = "";
	//array of messages
    private ArrayList messages = new ArrayList();
	//array of clients
    private ArrayList remoteDevices = new ArrayList();


    //adds remote device to arraylist remoteDevices
    public synchronized void addRemoteDevice(RemoteDevice rd){
        remoteDevices.add(rd);
    }

    //removes remote device from arraylist remoteDevices.
    public synchronized void deleteRemoteDevice(RemoteDevice rd){
        int i = remoteDevices.indexOf(rd);
        if (i != -1)
           remoteDevices.remove(i);
    }

    //String message is added to ArrayList messages, ArrayList reader awoken by notify().
    public synchronized void postMessage(RemoteDevice rd, String message){
        messages.add(message);
        notify();
    } 

    //deletes a retrieved message from the ArrayList, then is returned.
    public synchronized String getNextElementFromMessages() throws InterruptedException{
        while (messages.size()==0)
           wait();

        String message = (String) messages.get(0);
        messages.remove(0);
        return message;
    }

 

    //all remote devices will be sent a String message.
    private synchronized void sendMessageToAllDevices(String message){
        for (int i=0; i<remoteDevices.size(); i++) {
           RemoteDevice rd = (RemoteDevice) remoteDevices.get(i);
           rd.sender.addToMessages(message);
        }
    }

 

    //while loop allows for messages to be read and sent to all clients.
    @Override
    public void run(){
        try {
           while (true) {
               String message = getNextElementFromMessages();
               sendMessageToAllDevices(message);
               castMessage = message;
           }
        } catch (InterruptedException ie) {
        }
    }
}

 