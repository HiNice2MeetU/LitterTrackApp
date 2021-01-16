package dev.hiworld.littertrackingapp.Network;

import android.util.Log;
import java.util.ArrayList;
import java.util.LinkedList;

import dev.hiworld.littertrackingapp.Utility.UtilityManager;

public class MQManager {
    // Globals
    LinkedList<MQMsg> CommandQueue = new LinkedList<MQMsg>();
    ArrayList<MQTracker> ListenerList = new ArrayList<MQTracker>();

    // Make ID
    String SessionID;

    // TEST CONSTRUCTOR
    public MQManager() {
        SessionID = UtilityManager.GenorateID(20, "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890!@#$%^&*()");
        Log.d("MQManager", "Genorate Session ID: " + SessionID);
    }

    // MQThread Kill Switch
    private static volatile boolean MQTRunning = true;

    // Execute Single Command
    public synchronized String Execute(MQMsg Input) {
        // Test Data
        NotifyObserver(new MQMsg(new ArrayList<Object>(),SessionID,"A","Test With Correct SESSION ID"));
        NotifyObserver(new MQMsg(new ArrayList<Object>(),"69","A","Test With InCorrect SESSION ID"));
        NotifyObserver(new MQMsg(new ArrayList<Object>(),SessionID,"6969","Test With InCorrect TRANSACTION ID"));
        NotifyObserver(new MQMsg(new ArrayList<Object>(),SessionID,"A","Test With Correct TRANSACTION ID"));
        NotifyObserver(new MQMsg(new ArrayList<Object>(),SessionID,"ERR","Test With Correct TRANSACTION ID but it is a ERROR"));
        Log.d("MQManager", "Executed Test Data");
        return "A";
    }

    // Execute List of Commands
    public synchronized ArrayList<Integer> ExecuteList(ArrayList<MQMsg> Input) {
        return null;
    }

    // Add observers
    public void AddObserver(ArrayList<String> IDs, MQListener Listener) {
        // Create Main
        MQTracker Main = new MQTracker(IDs, Listener);

        // Add to list
        ListenerList.add(Main);

        // Log
        Log.d("MQManager", "Added Observer " + Main.toString());
    }

    // Add observer with 1 id
    public void AddObserver(String ID, MQListener Listener) {
        // Create Main
        MQTracker Main = new MQTracker(Listener);

        // Add to id list
        Main.TransactionID.add(ID);

        // Add to list
        ListenerList.add(Main);

        // Log
        Log.d("MQManager", "Added Observer " + Main.toString());
    }

    // Remove Observer
    public void RemoveObserver(MQListener Listener){
        ListenerList.remove(Listener);
    }

    // Notify Observer
    public void NotifyObserver(MQMsg Msg){
        // Check if has the same session id
        if (SessionID.equals(Msg.getSessionID())) {
            // Iterate through list of observers
            for (int i = 0; i<ListenerList.size();i++){
                // Get Current
                MQTracker Current = ListenerList.get(i);

                // Get Msg ID
                String TID = Msg.getTransactionID();

                // Check if current id list has the Msg id
                if (Current.getTransactionID().contains(TID)) {
                    // Log
                    Log.d("MQManager", "Found Match in Observer List to" + Msg.toString());

                    // Update Listeners
                    Current.getListener().Update(Msg);

                    // Log
                    Log.d("MQManager", "Msg sent to observer " + TID);

                } else if (TID.equals("ERR")) {
                    // Update Listeners if is tagged with error
                    Current.getListener().Error(Msg);

                    // Log
                    Log.d("MQManager", "Error Detected" + Msg.toString());

                } else {
                    // No match is found
                    Log.d("MQManager", "Match Not found");
                }
            }
        }
    }

    // Observer Interface
    public interface MQListener {
        // When new message comes in
        public void Update(MQMsg Msg);

        // When new error comes in
        public void Error(MQMsg Error);
    }

    // Start Network Thread
    private void StartNet() {
        // Create MqSession Object
        MQSession Sesh = new MQSession(2,SessionID);

        // Start Thread
        Thread NetworkOutThread = new Thread(new MQThread(Sesh));
        NetworkOutThread.setDaemon(true);
        NetworkOutThread.setName("NetThread@" + SessionID);
        NetworkOutThread.start();
    }

    // Class to be executed on different thread
    class MQThread implements Runnable {
        // Globals
        MQSession Sesh;

        // Constructor
        public MQThread(MQSession sesh) {
            Sesh = sesh;
        }

        // Main Func
        public void run(){
            // Execute
            while (MQTRunning) {
                synchronized (MQManager.this) {
                    // Sleep
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Log.e("MQManager", e.toString() + " at ReadThread");
                    }
                }
            }
        }
    }
}
