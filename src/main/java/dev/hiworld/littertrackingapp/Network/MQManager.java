package dev.hiworld.littertrackingapp.Network;

public class MQManager {
    // Globals
    ArrayList<MQMsg> ListenerList = new ArrayList<MQMsg>();

    // Execute Single Command
    public synchronized int Execute(MQMsg Input) {

    }

    // Execute List of Commands
    public synchronized ArrayList<Integer> Execute(MQMsg Input) {

    }

    // Start Network Thread
    private void StartNet() {

    }

    // Class to be executed on different thread
    class MQThread implements Runnable {

    }
}
