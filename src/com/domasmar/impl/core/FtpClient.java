package com.domasmar.impl.core;

import com.domasmar.impl.observers.newMessage.NewMessage;
import com.domasmar.impl.observers.newMessage.NewMessagesObserver;
import com.domasmar.impl.receivers.ConnectionReceiver;
import com.domasmar.impl.receivers.FileReceiver;
import com.domasmar.impl.receivers.MessageReceiver;
import com.domasmar.impl.senders.MessageSender;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by domas on 14.11.5.
 */
public class FtpClient {

    private String ip;
    private int port;
    private Socket socket;
    private boolean isLoggedIn = false;

    private MessageReceiver messageReceiver;
    private MessageSender messageSender;

    public FtpClient() {
    }

    public FtpClient(String ip, int port) {
        setIp(ip);
        setPort(port);
    }

    public void connect() throws Exception {
        if (!isConnected()) {
            socket = new Socket(ip, port);
            messageReceiver = new MessageReceiver(socket);
            if (messageReceiver.getState() == Thread.State.NEW) {
                messageReceiver.start();
            }
            messageSender = new MessageSender(socket);
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void close() {
        if (isConnected()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        messageSender.send(message);
    }

    public NewMessage waitForResponseCode(int responseCode) {
        // Might freeze
        NewMessage message;
        do {
            message = NewMessagesObserver.getInstance().getMessage();
        } while (message == null || message.getMessageCode() != responseCode);
        return message;
    }

    public NewMessage waitForResponseCodes(ArrayList<Integer> list) {
        // Might freeze
        NewMessage message;
        do {
            message = NewMessagesObserver.getInstance().getMessage();
        } while (message == null || !Helpers.codeInList(list, message.getMessageCode()));
        return message;
    }

    public int getLastResponseCode() {
        NewMessage message;
        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            message = NewMessagesObserver.getInstance().peek();
        } while (message == null);
        return message.getMessageCode();
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public boolean login(String username, String password) {
        if (getLastResponseCode() != 530) {
            waitForResponseCode(220);
        }
        messageSender.send("USER " + username);
        waitForResponseCode(331);
        messageSender.send("PASS " + password);
        if (getLastResponseCode() == 230) {
            isLoggedIn = true;
        } else {
            isLoggedIn = false;
        }
        return isLoggedIn();
    }

    public boolean changeWorkingDir(String dir) {
        sendMessage("CWD " + dir);
        ArrayList<Integer> responseCodeList = new ArrayList<Integer>();
        responseCodeList.add(250);
        responseCodeList.add(550);
        int response = waitForResponseCodes(responseCodeList).getMessageCode();
        if (response == 250) {
            return true;
        }
        return false;
    }

    public String[] getCurrentDirList() {
        String[] itemArray = new String[0];
        try {
            ConnectionReceiver connectionReceiver = new ConnectionReceiver();
            if (connectionReceiver.getState() == Thread.State.NEW) {
                connectionReceiver.start();
            }
            int port = connectionReceiver.getPort();
            sendMessage("PORT 127,0,0,1," + port / 256 + "," + port % 256);
            sendMessage("LIST");
            // If there was some errors
            // It will free in this place,
            // Because response 200 will never be received
            waitForResponseCode(200); // Port command successful;
            waitForResponseCode(150); // After this we get all information to connectionReceiver
            waitForResponseCode(226); // Finished sending information
            String dirList = connectionReceiver.getReceived();
            connectionReceiver.close();
            return dirList.split("\\n");
        } catch (Exception e) {
            return new String[0];
        }
    }

    public void downloadFile(String filename, String destination) {
        sendMessage("TYPE I"); // Set binary transfer
        waitForResponseCode(200);
        try {
            sendMessage("SIZE " + filename);
            long fileSize = Long.parseLong(waitForResponseCode(213).getMessageContent().trim());

            FileReceiver fileReceiver = new FileReceiver(destination, filename);
            fileReceiver.setFileSize(fileSize);
            int port = fileReceiver.getPort();
            fileReceiver.start();
            sendMessage("PORT 127,0,0,1," + port / 256 + "," + port % 256);
            sendMessage("RETR " + filename);
            waitForResponseCode(200); // Port command success

            // Response could be 150 (if file exists)
            // or 550 if file was not found
            ArrayList<Integer> responseCodeList = new ArrayList<Integer>();
            responseCodeList.add(150);
            responseCodeList.add(550);
            int response = waitForResponseCodes(responseCodeList).getMessageCode();
            if (response == 150) {
                waitForResponseCode(226); // Transfer complete
                // We have file in buffer, but not in actual file
                // So we need to wait some time before closing socket's stream
                while (fileReceiver.isWorking()) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            fileReceiver.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
