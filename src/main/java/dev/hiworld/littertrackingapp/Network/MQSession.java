package dev.hiworld.littertrackingapp.Network;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQSession implements MqttCallback {
    // Globals
    int Qos;
    MqttClient MqttClient;

    // Keep track of observers
    ArrayList<ResultListener> ListenerList = new ArrayList<ResultListener>();

    // Constructor
    public MQSession (int Qos) {
        this.Qos = Qos
    }

    public MQSession () {
        Qos = 2;
    }

    public interface ResultListener {

        // When a message is recieved
        public void Update(SocketResultSet Msg) {

        }

        // When error is encountered
        public void Error(int ID, Exception Err) {

        }
    }

    // Connect
    public void Connect(String Broker, String ClientId){
        try {
            // Create Persistence
            MemoryPersistence Persistence = new MemoryPersistence();

            // Create Client
            MqttClient = new MqttClient(Broker, ClientId, Persistence);

            // Set Client
            MqttConnectOptions ConnOpts = new MqttConnectOptions();
            ConnOpts.setCleanSession(true);

            // Create Callback
            MqttClient.setCallback(this);

            // Connect
            Log.d("MqSession","Connecting to broker: "+ Broker);
            MqttClient.connect(ConnOpts);
            System.out.println("Connected");
            return 0;
        } catch (Exception e) {
            // Error
            System.out.println(e.toString());
            return 1;
        }
    }

    // Publish
    public void Publish(String Msg, String Topic) {
        try {
            // Publish Message
            System.out.println("Publishing message: "+Content);
            MqttMessage Message = new MqttMessage(Content.getBytes());
            Message.setQos(Qos);
            MqttClient.publish(Topic, Message);
            System.out.println("Message published");
            return 0;
        } catch (Exception e) {
            // Error
            System.out.println(e.toString());
            return 2;
        }
    }

    // Disconnect
    public void Disconnect(){
        try {
            // Disconnect
            MqttClient.disconnect();
            Log.d("MQSession","Disconnected");
            System.exit(0);
            return 0;
        } catch (Exception e) {
            // Error
            System.out.println(e.toString());
            return 3;

        }
    }

    // Mqtt callback stuff
    public void connectionLost(java.lang.Throwable cause) {

    }

    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    public void messageArrived(String topic, MqttMessage message){

    }
}
