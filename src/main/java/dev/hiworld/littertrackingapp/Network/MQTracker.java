package dev.hiworld.littertrackingapp.Network;

import java.util.ArrayList;

import dev.hiworld.littertrackingapp.Network.NetworkTwo.MQManager;

public class MQTracker {
    // Globals
    protected ArrayList<String> TransactionID;
    protected MQManager.MQListener Listener;

    // Constructor
    public MQTracker(ArrayList<String> transactionID, MQManager.MQListener listener) {
        TransactionID = transactionID;
        Listener = listener;
    }

    public MQTracker(MQManager.MQListener listener) {
        Listener = listener;
        TransactionID = new ArrayList<String>();
    }

    // Getters + Setters
    public ArrayList<String> getTransactionID() {
        return TransactionID;
    }

    public void setTransactionID(ArrayList<String> transactionID) {
        TransactionID = transactionID;
    }

    public MQManager.MQListener getListener() {
        return Listener;
    }

    public void setListener(MQManager.MQListener listener) {
        Listener = listener;
    }

    @Override
    public String toString() {
        return "MQTracker{" +
                "TransactionID=" + TransactionID +
                ", Listener=" + Listener +
                '}';
    }
}
