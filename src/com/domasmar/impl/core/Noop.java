package com.domasmar.impl.core;

/**
 * Created by domas on 14.11.5.
 */
public class Noop extends Thread {

    private FtpClient client;

    public Noop(FtpClient client) {
        this.client = client;
        this.start();
    }

    public void run() {
        while (true) {
            if (client.isConnected() && client.isLoggedIn()) {
                try {
                    sleep(20000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                client.sendMessage("NOOP");
            } else {
                break;
            }
        }
    }
}
