package dev.hiworld.littertrackingapp.Network;

import android.util.Log;

import java.util.ArrayList;

import dev.hiworld.littertrackingapp.Utility.UtilityManager;

public class MQManager {
    // Globals
    ArrayList<MQMsg> ListenerList = new ArrayList<MQMsg>();

    // MQThread Kill Switch
    private static volatile boolean MQTRunning = true;

    // Execute Single Command
    public synchronized int Execute(MQMsg Input) {
        return 1;
    }

    // Execute List of Commands
    public synchronized ArrayList<Integer> ExecuteList(MQMsg Input) {
        return null;
    }

    // Start Network Thread
    private void StartNet() {
        // Make ID
        String SessionID = UtilityManager.GenorateID(10, "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890!@#$%^&*()");

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
