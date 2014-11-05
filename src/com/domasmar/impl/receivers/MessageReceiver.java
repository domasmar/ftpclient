package com.domasmar.impl.receivers;

import com.domasmar.impl.observers.newMessage.NewMessage;
import com.domasmar.impl.observers.newMessage.NewMessagesObserver;
import com.domasmar.interfaces.Receiver;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by domas on 14.11.5.
 */
public class MessageReceiver extends Receiver {

    private Socket socket;
    private DataInputStream inputStream;

    public MessageReceiver(Socket socket) throws Exception {
        inputStream = new DataInputStream(socket.getInputStream());
        this.start();
    }

    @Override
    public Object receive() {
        String message = "";
        byte messageChar;
        try {
            while ((messageChar = inputStream.readByte()) != 13) {
                message += (char) messageChar;
            }
            // Reading char - 10
            inputStream.readByte();
            return message;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void run() {
        while (true) {
            String message = (String) receive();
            if (!message.equals(null)) {
                System.out.println("Received message: " + message);
                NewMessagesObserver.getInstance().addMessage(new NewMessage(message));
            } else {
                try {
                    sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
