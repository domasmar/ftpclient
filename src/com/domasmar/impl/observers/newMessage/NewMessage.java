package com.domasmar.impl.observers.newMessage;

/**
 * Created by domas on 14.11.5.
 */
public class NewMessage {

    private int messageCode;
    private String messageContent;
    private String originalMessage;

    public NewMessage(String message) {
        originalMessage = message;
        parseMessage(message);
    }

    public String toString() {
        return messageCode + ":" + messageContent;
    }

    private void parseMessage(String message) {
        String messageCodeString = message.substring(0, 3).trim();
        try {
            messageCode = Integer.parseInt(messageCodeString);
        } catch (Exception e) {
            e.printStackTrace();
            messageCode = 0;
        }
        messageContent = message.substring(4).trim();
    }

    public int getMessageCode() {
        return messageCode;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public String getOriginalMessage() {
        return originalMessage;
    }
}
