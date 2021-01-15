package dev.hiworld.littertrackingapp.Network;

import java.util.ArrayList;

public class MQTracker {
    // Globals
    ArrayList<Integer> TransactionID;
    MQManager.MQListener Listener;

    // Constructor
    public MQTracker(ArrayList<Integer> transactionID, MQManager.MQListener listener) {
        TransactionID = transactionID;
        Listener = listener;
    }

    // Getters + Setters
    public ArrayList<Integer> getTransactionID() {
        return TransactionID;
    }

    public void setTransactionID(ArrayList<Integer> transactionID) {
        TransactionID = transactionID;
    }

    public MQManager.MQListener getListener() {
        return Listener;
    }

    public void setListener(MQManager.MQListener listener) {
        Listener = listener;
    }
}
