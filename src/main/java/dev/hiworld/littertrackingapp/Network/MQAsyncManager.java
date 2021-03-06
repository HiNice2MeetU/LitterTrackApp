package dev.hiworld.littertrackingapp.Network;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.MqttCallback;

import java.util.ArrayList;
import java.util.LinkedList;

import dev.hiworld.littertrackingapp.Utility.UtilityManager;

public class MQAsyncManager {
    // Globals
    String SessionID;
    private boolean Failed = false;



    // Queue List
    LinkedList <MQListMsg> MsgQueue = new LinkedList<MQListMsg>();

    // Client
    MQAsyncClient MQClient;

    // Constructor
    public MQAsyncManager() {
        // Make Sessiond ID
        SessionID = UtilityManager.GenorateID(20, "1234567890QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm,!@#$%^&*()-_=+");

        // Make async client
        MQClient = new MQAsyncClient(SessionID);
    }

    // Add a msg to the queue
    public String Add(MQMsg Input, IMqttActionListener SucessCall) {
        // genorate transaction ID
        String TansactionID = UtilityManager.GenorateID(10, "1234567890QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm,!@#$%^&*()-_=+");

        // Set transaciton id
        Input.setTransactionID(TansactionID);

        // Set session id
        Input.setSessionID(SessionID);

        // Add to list
        MsgQueue.addLast(new MQListMsg(SucessCall, Input));

        // Return
        return TansactionID;
    }

    // Execute the next msg in the queue
    public void Next() {
        if (MsgQueue.size() != 0) {

            // Get Current
            MQListMsg Current = MsgQueue.removeFirst();

            // Log
            Log.d("MQAsyncClient", "Executing Next: " + Current.toString());

            // Do
            Execute(Current);
        } else {
            Log.e("MQAsyncClient", "Cannot Next on null list");
        }
    }

    // Determine what to do with input
    private void Execute(MQListMsg RawMsg) {
        // Get MqMsg components
        MQMsg Msg = RawMsg.getMsg();
        ArrayList<Object> Params = Msg.getParams();
        String Cmd = Msg.getCmd();

        // Get Listener
        IMqttActionListener Listener = RawMsg.getListener();

        // Switch Statement to determine what to do
        if (!Failed) {
            switch (Cmd) {
                case "Connect":
                    // Connect
                    MQClient.Connect((String) Params.get(0), (MqttCallback) Params.get(1), (boolean) Params.get(2), Listener, (Context)Params.get(3));
                    Params.remove(3);
                    Msg.setParams(Params);
                    break;
                case "Disconnect":
                    // Disconnect
                    MQClient.Disconnect(Listener);

                    // Check Ping
                    MQClient.CheckPing(Listener);

                    break;
                case "Subscribe":
                    // Log
                    Log.d("MQAsyncClient", "Subbing@Executor");

                    // Check Ping
                    MQClient.CheckPing(Listener);

                    // Sub
                    MQClient.Subscribe(Listener);
                    break;
                default:
                    // Log
                    Log.d("MQAsyncClient", "Publishing@Executor");

                    // Check Ping
                    MQClient.CheckPing(Listener);

                    // Send to server
                    MQClient.Publish(Msg, Listener);
                    break;
            }
        }
    }

    class MQListMsg {
        private IMqttActionListener Listener;
        private MQMsg Msg;

        // Constructor
        public MQListMsg(IMqttActionListener listener, MQMsg msg) {
            Listener = listener;
            Msg = msg;
        }

        // Getters and Setters

        public IMqttActionListener getListener() {
            return Listener;
        }

        public void setListener(IMqttActionListener listener) {
            Listener = listener;
        }

        public MQMsg getMsg() {
            return Msg;
        }

        public void setMsg(MQMsg msg) {
            Msg = msg;
        }

        @Override
        public String toString() {
            return "MQListMsg{" +
                    "Listener=" + Listener +
                    ", Msg=" + Msg +
                    '}';
        }
    }

    // Getter setter for SessionID
    public String getSessionID() {
        return SessionID;
    }

    public void setSessionID(String sessionID) {
        SessionID = sessionID;
    }

    // Getters and Setters

    public boolean isFailed() {
        return Failed;
    }

    public void setFailed(boolean failed) {
        Failed = failed;
    }
}

