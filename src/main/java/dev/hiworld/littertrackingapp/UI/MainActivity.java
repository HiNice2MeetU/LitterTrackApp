package dev.hiworld.littertrackingapp.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Arrays;

import dev.hiworld.littertrackingapp.Network.Event;
import dev.hiworld.littertrackingapp.Network.MQAsyncClient;
import dev.hiworld.littertrackingapp.Network.MQAsyncManager;
import dev.hiworld.littertrackingapp.Network.MQMsg;
import dev.hiworld.littertrackingapp.Network.MQSession;
import dev.hiworld.littertrackingapp.Network.MQManager;
import dev.hiworld.littertrackingapp.Network.MsgType;
import dev.hiworld.littertrackingapp.Network.OldNetwork.ServerTransport;
import dev.hiworld.littertrackingapp.Network.OldNetwork.ServerExecutor;
import dev.hiworld.littertrackingapp.R;
import dev.hiworld.littertrackingapp.Utility.UtilityManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MqttCallback{
    // Test MQManager
    MQAsyncManager MQS = new MQAsyncManager();
    String PublishID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get Button
        Button Go2Camera = findViewById(R.id.CameraButton);
        Go2Camera.setOnClickListener(this);

        Button Go2Map = findViewById(R.id.MapButton);
        Go2Map.setOnClickListener(this);

        ServerTransport ST = ServerTransport.getInstance();
        ST.Start();

        // Create Test Callback
        IMqttActionListener ConnectCallback = new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.d("MQAsyncClient","Connection Sucess!");
                MQS.Next();
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.d("MQAsyncClient","Connection Failiure: " + exception.toString());
                //MQS.Next();
            }
        };

        IMqttActionListener PublishCallback = new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.d("MQAsyncClient","Publish Sucess!");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.d("MQAsyncClient","Publish Failiure: " + exception.toString());
                //MQS.Next();
            }
        };

        IMqttActionListener DisconnectCallback = new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.d("MQAsyncClient","Disconnect Sucess!");
                //MQS.Next();
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.d("MQAsyncClient","Disconnect Failiure: " + exception.toString());
                //MQS.Next();
            }
        };

        IMqttActionListener SubscribeCallback = new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.d("MQAsyncClient","Subscribe Sucess!");
                MQS.Next();
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.d("MQAsyncClient","Subscribe Failiure: " + exception.toString());
                //MQS.Next();
            }
        };

        MQS.Add(new MQMsg(new ArrayList<Object>(Arrays.asList("tcp://192.168.6.133:1883", this, false)), "Connect"),ConnectCallback);
        MQS.Add(new MQMsg(new ArrayList<Object>(Arrays.asList()), "Subscribe"), SubscribeCallback);
        PublishID = MQS.Add(new MQMsg(new ArrayList<Object>(Arrays.asList(new Event(69,69,"SixetyNine"), new Event(96,96,"NinetySix"))), "Ladida"), PublishCallback);
        MQS.Add(new MQMsg(new ArrayList<Object>(Arrays.asList(new Event(69,69,"SixetyNine"), new Event(96,96,"NinetySix"))), "Disconnect"), DisconnectCallback);

        MQS.Next();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.CameraButton:
                // Test TODO remove this
                MQS.Next();

                // Send to Camera Activity
                Intent i = new Intent(this, CameraView.class);
                Log.d("Main", "Sucessfully opened Camera App");
                startActivity(i);
                break;
            case R.id.MapButton:
                // Test TODO remove this
                MQS.Next();

                // Send to Map Activity
                Intent t = new Intent(this, Mapy2.class);
                Log.d("Main", "Sucessfully opened Map App");
                startActivity(t);
                break;
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.e("MQAsyncClient", "Lost Connection");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        // Decode the json into MqMsg
        MQMsg FormattedMsg = MQAsyncClient.DecodeResult(message.toString());

        // Check if msg isnt null
        if (FormattedMsg!=null) {
            // Chck if msg is valid
            if (MQAsyncClient.Validate(FormattedMsg, PublishID, MQS.getSessionID()) == MsgType.YES) {
                // Log
                Log.d("MQAsyncClient", "Raw Message Arrived@" + topic + ": " + message.toString());
                Log.d("MQAsyncClient", "Formatted Message Arrived@" + topic + ": " + FormattedMsg.toString());
            }
        } else {
            // Log a null formatted msg
            Log.e("MQAsyncClient", "FormattedMsg == null");
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
