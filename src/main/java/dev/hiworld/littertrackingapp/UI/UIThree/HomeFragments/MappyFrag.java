package dev.hiworld.littertrackingapp.UI.UIThree.HomeFragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import dev.hiworld.littertrackingapp.Network.Event;
import dev.hiworld.littertrackingapp.Network.MQAsyncClient;
import dev.hiworld.littertrackingapp.Network.MQAsyncManager;
import dev.hiworld.littertrackingapp.Network.MQMsg;
import dev.hiworld.littertrackingapp.Network.MsgType;
import dev.hiworld.littertrackingapp.R;
import dev.hiworld.littertrackingapp.Utility.TrashConA;
import dev.hiworld.littertrackingapp.Utility.UtilityManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;


public class MappyFrag extends Fragment {

    // Globals
    private String PublishID;
    private Gson gson = new Gson();
    private GoogleMap Gmap;

    // Networking
    MQAsyncManager MQM = new MQAsyncManager();

    // Make command listener
    IMqttActionListener MQListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
            Log.d("CameraFrag", "MQM Action Sucesfull");
            MQM.Next();
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            Log.e("CameraFrag", "MQM Action failed");
            NotifyNetErr();

            MQM.setFailed(true);
        }
    };

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            // Default Code
            //LatLng sydney = new LatLng(-34, 151);
            //googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            //googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            // Networking
            MQM.setFailed(false);
            MQM.Add(new MQMsg(new ArrayList<Object>(Arrays.asList("tcp://192.168.6.133:1883", new MqttCallback(){

                @Override
                public void connectionLost(Throwable cause) {
                    NotifyNetErr();
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // Get Message
                    String RawMsg = message.toString();

                    // Decode the json into MqMsg
                    MQMsg FormattedMsg = MQAsyncClient.DecodeResult(RawMsg);

                    // Log
                    Log.d("MappyFrag", "Msg Arrived: " + RawMsg);

                    // Check Formatted Msg != null
                    if (FormattedMsg != null) {
                        // Check if is for publish id
                        if (MQAsyncClient.Validate(FormattedMsg, PublishID, MQM.getSessionID()) == MsgType.YES) {
                            // Log
                            Log.d("MappyFrag", "Formatted Msg Arrived: " + FormattedMsg.toString());

                            // Turn ArrayList<Object> into ArrayList<Event>
                            LinkedList<Event>TempEvents = new LinkedList<Event>();
                            for (Object object : FormattedMsg.getParams()) {
                                TempEvents.add((Event)object);
                                Log.d("MappyFrag", "Adding to temp events: " + object.toString());
                                MQM.setFailed(false);
                            }

                            // Start Adder Thread
                            StartMarkThread(TempEvents, googleMap);
                        }
                    } else {
                        Log.d("MappyFrag", "Formatted Msg Failed: " + RawMsg);
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }

            }, false)), "Connect"),MQListener);
            MQM.Add(new MQMsg(new ArrayList<Object>(Arrays.asList()), "Subscribe"), MQListener);
            PublishID = MQM.Add(new MQMsg(new ArrayList<Object>(), "GetAll"), MQListener);
            MQM.Next();

            // Set info window
            TrashConA CInfoWin = new TrashConA(getActivity());
            googleMap.setInfoWindowAdapter(CInfoWin);
            Gmap = googleMap;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mappy, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get Map Fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        // Check if map fragment != null
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private void StartMarkThread(LinkedList<Event>Data,GoogleMap map){
        // Create instance
        MarkerAdder MAddT = new MarkerAdder(50, false, Data,map);

        // Marker add thread
        Thread NetworkOutThread = new Thread(MAddT);
        NetworkOutThread.setDaemon(true);
        NetworkOutThread.setName("MarkerAdd");
        NetworkOutThread.start();
    }

    // Marker Adder thead
    class MarkerAdder implements Runnable {
        // Global
        long AddDelay = 500;
        boolean LookCamera = true;
        LinkedList<Event> DBData = new LinkedList<Event>();
        GoogleMap mMap;

        // Constructors
        public MarkerAdder(GoogleMap mMap) {
            this.mMap = mMap;

            // Add Test Data
            DBData.addLast(new Event(0.0, 0.0, null));
            DBData.addLast(new Event(59.0, 59.0, null));
            DBData.addLast(new Event(21.0, 0.0, null));
            DBData.addLast(new Event(23.0, 56.0, null));
            DBData.addLast(new Event(42.424, 23.234, null));
            DBData.addLast(new Event(13.2324, 14.521, null));
            DBData.addLast(new Event(21.0, 23.0, null));
            DBData.addLast(new Event(20.0, 40.0, null));
            DBData.addLast(new Event(50.0, 50.0, null));
        }

        public MarkerAdder(LinkedList<Event> DBData, GoogleMap mMap) {
            this.DBData = DBData;
            this.mMap = mMap;
        }

        public MarkerAdder(long addDelay, boolean lookCamera, LinkedList<Event> DBData, GoogleMap mMap) {
            AddDelay = addDelay;
            LookCamera = lookCamera;
            this.DBData = DBData;
            this.mMap = mMap;
        }

        // Main run loop
        public void run() {
            // Add Markers
            AddMarkers(DBData,Gmap, AddDelay, LookCamera);

            // Log
            Log.d("MappyFrag", "Finished adding markers");
        }
    }

    // Add Markers
    public void AddMarkers(LinkedList<Event> DBData, GoogleMap googleMap, long AddDelay, boolean LookCamera) {
        // Loop till all markers been added
        while (DBData.size() > 0) {
            // Get Current
            Event CEvent = DBData.removeFirst();

            // Clear it in list
            //DBData.remove(0);

            // Extract Lng & Lat
            LatLng CPos = new LatLng(CEvent.getLongitude(),CEvent.getLatitude());

            // Set Img
            String Bmp = CEvent.getBmp();

            // Add Marker on main thread
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Add Marker
                    Marker AddedMarker = googleMap.addMarker(new MarkerOptions()
                            .position(CPos)
                            .title("Marker at " + CPos.toString())
                            // Set Image
                            .snippet(gson.toJson(CEvent)));

                    // Log
                    Log.d("MappyFrag", "Added Marker at " + CPos.toString());

                    // Move Camera
                    if (LookCamera) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(CPos));
                    }
                }
            });


            // Log
            Log.d("MappyFrag", "Request Add Marker at " + CPos.toString() + ", " + String.valueOf(DBData.size()) + " Markers left");

            //Sleep
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Log.e("MappyFrag", e.toString());
            }
        }
    }

    // Notify user of error
    private void NotifyNetErr() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Toast
                Toast.makeText(getActivity(), getString(R.string.info_connection_failed), Toast.LENGTH_SHORT).show();

                // Log
                Log.d("MappyFrag", "Showed NotifyNetErr");

                // Set Failed
                MQM.setFailed(true);
            }
        });
    }

    // Disconnect from network when fragment is done
    public void onStop() {
        // Make disconnect listener
        Log.d("CameraFrag", "Disconnecting on destroy");
        IMqttActionListener MQDisconnect = new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.d("CameraFrag", "MQM Action Sucesfull");
                MQM.Next();
                //CamAcceptance.super.onStop();
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.e("CameraFrag", "MQM Action failed");
                //NotifyNetErr();
                //CamAcceptance.super.onStop();
            }
        };

        if (!MQM.isFailed()) {
            MQM.Add(new MQMsg("Disconnect"), MQDisconnect);
            MQM.Next();
        }

        MappyFrag.super.onStop();
    }
}