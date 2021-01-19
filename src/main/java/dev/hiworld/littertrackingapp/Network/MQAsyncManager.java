package dev.hiworld.littertrackingapp.Network;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;

import java.util.LinkedList;

public class MQAsyncManager {
    // Queue List
    LinkedList <MQListMsg> MsgQueue = new LinkedList<MQListMsg>();

    // Add a msg to the queue
    public void Add(MQMsg Input, IMqttActionListener SucessCall) {

    }

    // Execute the next msg in the queue
    public void Next() {

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

