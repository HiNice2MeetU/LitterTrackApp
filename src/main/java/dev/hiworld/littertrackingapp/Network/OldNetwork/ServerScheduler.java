package dev.hiworld.littertrackingapp.Network.OldNetwork;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import dev.hiworld.littertrackingapp.Network.SocketResultSet;

public class ServerScheduler implements ServerTransport.ServerListener {
    // Globals
    private ServerTransport ST = ServerTransport.getInstance();
    private ArrayList<IDSCombo> ScheduleListeners = new ArrayList<IDSCombo>();
    //private ConnectionAttempts = 0;

    // Default Settings
    private long IdleTime = 1069;
    private long TimeoutTime = 10000;

    // Constructor
    public ServerScheduler(){
        ST.AddObserver(this);
    }

    // Interfaces
    public interface ResultListener {
        // When there is a acknolegment statement
        public void OnSucess(SocketResultSet Msg);

        // When no return is reached
        public void OnError();

        // When waiting more than idle threshold
        public void OnIdiling();
    }

    // Add an observer
    public IDSCombo AddObserver(int ID, ResultListener Listener) {
        // Make class
        IDSCombo Return = new IDSCombo(ID,Listener);

        // Add to observer List
        ScheduleListeners.add(Return);

        // Setup timeout task
        TimerTask TimeoutTask = new TimerTask() {
            public void run() {
                NotifyObservers(null, ResultState.ERROR, ID);
            }
        };

        // Steup idle task
        TimerTask IdleTask = new TimerTask() {
            public void run() {
                NotifyObservers(null, ResultState.IDLE, ID);
            }
        };

        // Make timer object
        Timer Timey = new Timer("ServerScheduler for process " + String.valueOf(ID));

        // Schedule Idle and timeout times
        Timey.schedule(TimeoutTask, TimeoutTime);
        Timey.schedule(IdleTask, IdleTime);

        // Return
        return Return;
    }

    // Remove an observer
    public void RemoveObserver(ResultListener Listener) {
        // Iterate through list
        for (int i = 0; i < ScheduleListeners.size(); i++){
            // Get Current
            IDSCombo Current = ScheduleListeners.get(i);

            // Check if equals
            if (Current.getListener() == Listener){
                // Delete if are
                ScheduleListeners.remove(Current);
            }
        }
    }

    public void Close(){
        //ST.Execute(new SocketResultSet("Disconnect"));
        //ST.Execute(new SocketResultSet("Close"));
        ST.RemoveObserver(this);
    }

    // Notify all observers
    public void NotifyObservers(SocketResultSet Msg, ResultState State, int ID) {
        // Iterate through every entry
        for (int i=0;i<ScheduleListeners.size();i++){
            // Get Current
            IDSCombo CurrentTop = ScheduleListeners.get(i);

            // Notify right func in interface
            if (CurrentTop.getID() == ID) {
                // Get listenr
                ResultListener Current = CurrentTop.getListener();

                // If id match
                switch (State) {
                    case SUCESS:
                        Current.OnSucess(Msg);
                        break;
                    case ERROR:
                        Current.OnError();
                        break;
                    case IDLE:
                        Current.OnIdiling();
                        break;
                }
            }
        }
    }

    // Result state enum
    enum ResultState {
        SUCESS,
        ERROR,
        IDLE
    }

    // Overidden server transport listenr
    public void Update(SocketResultSet Msg){
        // Notify observer
        NotifyObservers(Msg, ResultState.SUCESS, Msg.getId());
    }

    // Function to try to reconnect
    public ConnectionStates Reconnect(ArrayList ConnectionDetails, int ConnectionAttempts){
        if (ConnectionAttempts < 3) {
            //ConnectionAttempts++;
            return ConnectionStates.RECONNECTING;
        } else {
            ConnectionAttempts = 0;
            return ConnectionStates.ERROR;
        }
    }
}
