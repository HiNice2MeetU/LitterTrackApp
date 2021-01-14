package dev.hiworld.littertrackingapp.Network;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import dev.hiworld.littertrackingapp.Network.OldNetwork.SocketResultSet;
import dev.hiworld.littertrackingapp.Utility.UtilityManager;

public class MQSession implements IMqttMessageListener {
    // Globals
    int Qos;
    String ID;
    IMqttClient MqttClient;
    String RecieveTopic = "Server";
    String SendTopic = "Client";

    // Keep track of observers
    ArrayList<ResultListener> ListenerList = new ArrayList<ResultListener>();

    // Constructor
    public MQSession (int Qos) {
        this.Qos = Qos;
        ID = UtilityManager.GenorateID(10, "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890!@#$%^&*()");
    }

    public MQSession () {
        Qos = 2;
        ID = UtilityManager.GenorateID(10, "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890!@#$%^&*()");
    }

    public interface ResultListener {

        // When a message is recieved
        public void Update(SocketResultSet Msg);

        // When error is encountered
        public void Error(int ID, Exception Err);
    }

    // Connect
    public void Connect(String Broker){
        try {
            // Create Persistence
            MemoryPersistence Persistence = new MemoryPersistence();

            // Create Client
            MqttClient = new MqttClient(Broker, ID, Persistence);

            // Set Client
            MqttConnectOptions ConnOpts = new MqttConnectOptions();
            ConnOpts.setCleanSession(true);
            ConnOpts.setAutomaticReconnect(true);
            ConnOpts.setConnectionTimeout(10);

            // Create Callback
            //MqttClient.setCallback(this);

            // Connect
            Log.d("MqSession","Connecting to broker: "+ Broker);
            MqttClient.connect(ConnOpts);
            System.out.println("Connected");

            // Add Subscriber
            MqttClient.subscribe(RecieveTopic, Qos, this);

            //return 0;
        } catch (Exception e) {
            // Error
            Log.e("MQSession",e.toString());
            //return 1;
        }
    }

    // Publish
    public void Publish(String Content, String Topic) {
        try {

            if (!MqttClient.isConnected()) {
                throw new MqttException(1);
            }

            // Publish Message
            Log.d("MQSession","Publishing message: "+Content);

            // Msg Params
            MqttMessage Message = new MqttMessage(Content.getBytes());
            Message.setQos(Qos);

            // TODO Set Retained to true at launch
            Message.setRetained(true);

            // Public / Log
            MqttClient.publish(Topic, Message);
            Log.d("MQSession","Message published");
            //return 0;
        } catch (Exception e) {
            // Error
            System.out.println(e.toString());
            //return 2;
        }
    }

    // Publish Without Topic
    public void Publish(String Content) {
        try {

            if (!MqttClient.isConnected()) {
                throw new MqttException(1);
            }

            // Publish Message
            Log.d("MQSession","Publishing message: "+Content);

            // Msg Params
            MqttMessage Message = new MqttMessage(Content.getBytes());
            Message.setQos(Qos);

            // TODO Set Retained to true at launch
            Message.setRetained(true);

            // Public / Log
            MqttClient.publish(SendTopic, Message);
            Log.d("MQSession","Message published");
            //return 0;
        } catch (Exception e) {
            // Error
            System.out.println(e.toString());
            //return 2;
        }
    }

    // Disconnect
    public void Disconnect(){
        try {
            // Disconnect
            MqttClient.disconnect();
            Log.d("MQSession","Disconnected");
            System.exit(0);
            //return 0;
        } catch (Exception e) {
            // Error
            System.out.println(e.toString());
            //return 3;

        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        try {
            // Get Payload
            byte[] payload = message.getPayload();

            // Get String
            String RawMsg = new String(payload, StandardCharsets.UTF_8);

            // Log
            Log.d("MQSession", "Msg Recieved: " + message);
        } catch (Exception e) {
            // Error
            Log.e("MQSession", e.toString());
        }
    }
}
