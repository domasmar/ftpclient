package com.domasmar.impl.senders;

import com.domasmar.interfaces.Sender;

import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Created by domas on 14.11.5.
 */
public class MessageSender extends Sender {

    private DataOutputStream outputStream;

    public MessageSender(Socket socket) throws Exception {
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void send(Object object) {
        String message = object.toString();
        try {
            System.out.println("Sent message: " + message);
            outputStream.writeBytes(message + '\n');
        } catch (Exception e) {
            System.out.println("Nepavyko išsiųsti žinutes į serverį");
            e.printStackTrace();
        }
    }
}
