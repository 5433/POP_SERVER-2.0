package pop.server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Sender extends Thread{
    private ArrayList messagesToSend = new ArrayList(); 
    private MessageLogger messageLogger;
    private RemoteDevice remoteDevice;
    private PrintWriter printer;

    public Sender(RemoteDevice rd, MessageLogger ml) throws IOException{
        remoteDevice = rd;
        messageLogger = ml;
        Socket socket = remoteDevice.socket;
        printer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    //adds String message to arraylist messagesToSend.
    public synchronized void addToMessages(String message){
        messagesToSend.add(message);
        notify();
    }

    //removes message from arraylist, and returns String message.
    private synchronized String nextElementToSend() throws InterruptedException{
        while (messagesToSend.size()==0)
           wait();

        String message = (String) messagesToSend.get(0);
        messagesToSend.remove(0);
        return message;
    }
    
    //send String message to Device using PrintWriter.
    private void sendMessageToDevice(String message){
        printer.println(message);
        printer.flush();
    }
    
    public void run(){
        try {
           while (!isInterrupted()) {
               String message = nextElementToSend();
               sendMessageToDevice(message);
           }
        } catch (Exception e) {
        }
        
        remoteDevice.listener.interrupt();
        messageLogger.deleteRemoteDevice(remoteDevice);
    }
}