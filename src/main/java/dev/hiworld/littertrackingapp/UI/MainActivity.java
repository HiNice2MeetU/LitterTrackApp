package dev.hiworld.littertrackingapp.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import dev.hiworld.littertrackingapp.Network.MQMsg;
import dev.hiworld.littertrackingapp.Network.MQSession;
import dev.hiworld.littertrackingapp.Network.MQManager;
import dev.hiworld.littertrackingapp.Network.OldNetwork.ServerTransport;
import dev.hiworld.littertrackingapp.Network.OldNetwork.ServerExecutor;
import dev.hiworld.littertrackingapp.R;
import dev.hiworld.littertrackingapp.Utility.UtilityManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get Button
        Button Go2Camera = (Button) findViewById(R.id.CameraButton);
        Go2Camera.setOnClickListener(this);

        Button Go2Map = (Button) findViewById(R.id.MapButton);
        Go2Map.setOnClickListener(this);

        ServerTransport ST = ServerTransport.getInstance();
        ST.Start();

        // Send Thread
        Thread NetworkOutThread = new Thread(new Test());
        NetworkOutThread.setDaemon(true);
        NetworkOutThread.setName("NetSecure");
        NetworkOutThread.start();

        // Test MQManager
        MQManager MQM = new MQManager();

        MQM.AddObserver("A", new MQManager.MQListener() {
            @Override
            public void Update(MQMsg Msg) {
                Log.d("MQManager", "Message Recieved at MainActivity: " + Msg.toString());
            }

            @Override
            public void Error(MQMsg Error) {
                Log.e("MQManager", "Error Recieved at MainActivity: " + Error.toString());
                MQM.RemoveObserver(this);
            }
        });

        MQM.Execute(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.CameraButton:
                // Send to Camera Activity
                Intent i = new Intent(this, CameraView.class);
                Log.d("Main", "Sucessfully opened Camera App");
                startActivity(i);
                break;
            case R.id.MapButton:
                // Send to Map Activity
                Intent t = new Intent(this, Mapy2.class);
                Log.d("Main", "Sucessfully opened Map App");
                startActivity(t);
                break;
        }
    }

    class Test implements Runnable {
        @Override
        public void run() {
            //ServerExecutor.SecureExecute();
            MQSession Sesh = new MQSession(2);

            // Execute
            Sesh.Connect("tcp://192.168.6.133:1883");
            Sesh.Publish("Hello Server");

            // TODO Reove Test Code
            Sesh.AddObserver(new MQSession.ResultListener() {
                @Override
                public void Update(String Msg) {
                    Log.d("MainActivity", "Recieved: " + Msg);
                }
            });

            while (true) {

            }
        }
    }
}
