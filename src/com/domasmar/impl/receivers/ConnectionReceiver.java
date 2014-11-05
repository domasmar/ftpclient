package com.domasmar.impl.receivers;

import com.domasmar.interfaces.Receiver;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by domas on 14.11.5.
 */
public class ConnectionReceiver extends Receiver {

    private int port;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataInputStream inputStream;

    private String received;

    public ConnectionReceiver() throws Exception {
        serverSocket = new ServerSocket(0);
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    @Override
    public Object receive() {
        String message = "";
        byte messageChar;
        try {
            try {
                while (true) {
                    messageChar = inputStream.readByte();
                    message += (char) messageChar;
                }
            } catch (EOFException e) {
            }
            return message;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        try {
            clientSocket.close();
            serverSocket.close();
        } catch (Exception e) {
        }
    }

    public void run() {
        try {
            clientSocket = serverSocket.accept();
            inputStream = new DataInputStream(clientSocket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        received = (String) receive();
    }

    public String getReceived() {
        return received;
    }
}
