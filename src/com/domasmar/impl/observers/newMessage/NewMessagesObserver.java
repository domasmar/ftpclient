package com.domasmar.impl.observers.newMessage;

import java.util.ArrayList;

/**
 * Created by domas on 14.11.5.
 */
public class NewMessagesObserver {

    private ArrayList<NewMessage> messageList;
    private static NewMessagesObserver newMesssageObserver;

    private NewMessagesObserver() {
        messageList = new ArrayList<NewMessage>();
    }

    public static NewMessagesObserver getInstance() {
        if (newMesssageObserver == null) {
            newMesssageObserver = new NewMessagesObserver();
        }
        return newMesssageObserver;
    }

    public boolean isEmpty() {
        return messageList.isEmpty();
    }

    public NewMessage getMessage() {
        if (!isEmpty()) {
            return messageList.remove(0);
        }
        return null;
    }

    public NewMessage peek() {
        if (messageList.size() != 0) {
            return messageList.get(messageList.size() - 1);
        }
        return null;
    }

    public void addMessage(NewMessage message) {
        messageList.add(message);
    }

}
