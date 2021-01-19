package dev.hiworld.littertrackingapp.Network;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.MqttCallback;

import java.util.ArrayList;
import java.util.LinkedList;

public class MQAsyncManager {
    // Queue List
    LinkedList <MQListMsg> MsgQueue = new LinkedList<MQListMsg>();

    // Client
    MQAsyncClient MQClient = new MQAsyncClient();

    // Add a msg to the queue
    public void Add(MQMsg Input, IMqttActionListener SucessCall) {
        MsgQueue.addLast(new MQListMsg(SucessCall, Input));
    }

    // Execute the next msg in the queue
    public void Next() {

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
        switch (Cmd) {
            case "Connect":
                // Connect
                MQClient.Connect((String)Params.get(0), (MqttCallback)Params.get(1), true, Listener);
                break;
            case "Disconnect":
                // Disconnect
                MQClient.Disconnect(Listener);
                break;
            default:
                // Send to server
                MQClient.Publish(Msg, Listener);
                break;
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
    }
}

