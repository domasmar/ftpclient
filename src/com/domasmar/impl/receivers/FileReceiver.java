package com.domasmar.impl.receivers;

import com.domasmar.interfaces.Receiver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by domas on 14.11.5.
 */
public class FileReceiver extends Receiver {

    public final int BUFFER_SIZE = 64000;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataInputStream inputStream;
    private String directory;
    private String filename;
    private File file;

    private long fileSize;
    private long timeElapsed;
    private boolean working;

    public FileReceiver(String directory, String filename) throws Exception {
        this.directory = directory;
        this.filename = filename;
        serverSocket = new ServerSocket(0);
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public String getFilename() {
        int index = 0;
        String fullPathFilename;
        String filenameModified = filename;
        filenameModified = filenameModified.indexOf('/') != -1
                ? filenameModified.substring(filenameModified.indexOf('/') + 1)
                : filenameModified;
        File file;
        while (true) {
            fullPathFilename = directory + File.separator + filenameModified;
            file = new File(fullPathFilename);
            if (file.exists() && !file.isDirectory()) {
                filenameModified = index + "_" + filename;
            } else {
                break;
            }
            index++;
        }
        return fullPathFilename;
    }

    @Override
    public Object receive() {
        working = true;
        byte messageChar;
        File file = new File(getFilename());
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            long startTime = System.currentTimeMillis();
            byte[] buffer = new byte[BUFFER_SIZE];
            long bytesRemaining = fileSize;
            while (bytesRemaining != 0) {
                try {
                    int length = inputStream.read(buffer);
                    fileOutputStream.write(buffer, 0, length);
                    bytesRemaining -= length;
                    // +1 is used to avoid division by zero
                    long bSpeed = (fileSize - bytesRemaining) / ((System.currentTimeMillis() - startTime + 1));
                    System.out.print("Bytes remaining: " + bytesRemaining + " Speed: " + bSpeed / 1024.0 + "\r");
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            timeElapsed = System.currentTimeMillis() - startTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        working = false;
        return file;
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
        file = (File) receive();
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getTimeElapsed() {
        return timeElapsed;
    }

    public boolean isWorking() {
        return working;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
