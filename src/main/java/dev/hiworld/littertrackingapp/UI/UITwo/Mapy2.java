package dev.hiworld.littertrackingapp.UI.UITwo;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import dev.hiworld.littertrackingapp.Network.Event;
import dev.hiworld.littertrackingapp.Network.NetworkOne.ServerScheduler;
import dev.hiworld.littertrackingapp.Network.NetworkOne.ServerTransport;
import dev.hiworld.littertrackingapp.Network.NetworkOne.SocketResultSet;
import dev.hiworld.littertrackingapp.R;
import dev.hiworld.littertrackingapp.Utility.TrashConA;
import dev.hiworld.littertrackingapp.Utility.UtilityManager;

public class Mapy2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TrashConA CInfoWin;
    private UtilityManager UM = new UtilityManager();
    private ServerScheduler SS = new ServerScheduler();
    private ServerTransport ST = ServerTransport.getInstance();

    // TESTING
    private LinkedList<SocketResultSet> TestData = new LinkedList<SocketResultSet>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapy2);
        CInfoWin = new TrashConA(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Add Test Data
        TestData.addLast(new SocketResultSet("", new ArrayList(Arrays.asList(new Event(0.0, 0.0, null)))));
        TestData.addLast(new SocketResultSet("", new ArrayList(Arrays.asList(new Event(59.0, 59.0, null)))));
        TestData.addLast(new SocketResultSet("", new ArrayList(Arrays.asList(new Event(21.0, 0.0, null)))));
        TestData.addLast(new SocketResultSet("", new ArrayList(Arrays.asList(new Event(23.0, 56.0, null)))));
        TestData.addLast(new SocketResultSet("", new ArrayList(Arrays.asList(new Event(42.424, 23.234, null)))));
        TestData.addLast(new SocketResultSet("", new ArrayList(Arrays.asList(new Event(13.2324, 14.521, null)))));
        TestData.addLast(new SocketResultSet("", new ArrayList(Arrays.asList(new Event(21.0, 23.0, null)))));
        TestData.addLast(new SocketResultSet("", new ArrayList(Arrays.asList(new Event(20.0, 40.0, null)))));
        TestData.addLast(new SocketResultSet("", new ArrayList(Arrays.asList(new Event(50.0, 50.0, null)))));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set custom window contents
        mMap.setInfoWindowAdapter(CInfoWin);

        // Notify user that markers are being added
        Toast.makeText(Mapy2.this, "Adding Markers to map", Toast.LENGTH_SHORT).show();

        StartMarkThread();

    }

    private void StartMarkThread(){
        // Create instance
        MarkerAdderThread MAddT = new MarkerAdderThread();
        //MAddT.AddObserver(this);
        // Marker add thread
        Thread NetworkOutThread = new Thread(MAddT);
        NetworkOutThread.setDaemon(true);
        NetworkOutThread.setName("MarkerAdd");
        NetworkOutThread.start();
    }

    // Marker Adder thead
    class MarkerAdderThread implements Runnable   {

        // Main run loop
        public void run(){
            // Get Data and call function to iterate through list
            // Get server transport


            // Log
            Log.d("MapyClass2", "Finished adding markers");
        }

        public void CloseConnection(){
            ST.Execute(new SocketResultSet("Disconnect"));
            ST.Execute(new SocketResultSet("Close"));
        }

        // Add Markers
        public void AddMarkers(ArrayList<Event> DBData){
            // Loop till all markers been added
            while (DBData.size() > 0) {
                // Get Current
                Event CEvent = DBData.get(0);

                // Clear it in list
                DBData.remove(0);

                // Get parmas
                //ArrayList CParam = Current.getParam();

                // Get Current Event
                //Event CEvent = (Event)CParam.get(0);

                // Extract Lng & Lat
                LatLng CPos = new LatLng(CEvent.getLatitude(), CEvent.getLongitude());

                // Set Img
                String Bmp = CEvent.getBmp();

                // Add Marker
                Mapy2.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Add Marker
                        mMap.addMarker(new MarkerOptions().position(CPos).title("Marker at " + CPos.toString()).snippet("If you are seeing this message something has gone wrong!"));
                        Log.d("MapyClass2", "Added Marker at " + CPos.toString());

                        // Test Move Camera
                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(CPos));

                        // Notify observers new calcualted pos
                        //NotifyObservers(CPos, Bmp);
                    }
                });



                // Log
                Log.d("MapyClass2", "Request Add Marker at " + CPos.toString() + ", " + String.valueOf(DBData.size())+ " Markers left");

                //Sleeeeeeeeeeeeeeeeeeeeeeeeeeeep
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Log.e("MapyClass2", e.toString());
                }
            }
        }



        // Old Connection Method
        public void OldConnect(){
            // Get Connect Id
            int ConnectID = ST.Execute(new SocketResultSet("Connect", EventMeta2.ConnectionDetails));

            SS.AddObserver(ConnectID, new ServerScheduler.ResultListener() {
                // When there is a acknolegment statement
                public void OnSucess(SocketResultSet Msg) {
                    // Remove Observer
                    SS.RemoveObserver(this);

                    // Execute Get All to server
                    int GetID = ST.Execute(new SocketResultSet("GetAll"));

                    // Log
                    Log.d("MapyClass2", "connection ok");

                    // Attach observer
                    SS.AddObserver(GetID, new ServerScheduler.ResultListener() {
                        public void OnSucess(SocketResultSet Msg) {
                            // Remove this observer
                            SS.RemoveObserver(this);

                            // Return function
                            AddMarkers((ArrayList<Event>)Msg.getParam().get(0));

                            // Close
                            SS.Close();
                            CloseConnection();

                            // Log
                            Log.d("MapyClass2", "Info retrievel request ok");
                        }

                        public void OnError() {
                            // Notify User
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(Mapy2.this, "Could not retrieve infomation, please try again later", Toast.LENGTH_SHORT).show();
                                }
                            });

                            // Remove Observer
                            SS.RemoveObserver(this);
                            SS.Close();
                            CloseConnection();
                        }

                        public void OnIdiling() {
                            // Notify User
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(Mapy2.this, "Trying to retrieve infomation, please wait", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }

                public void OnError(){
                    // Notify User
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(Mapy2.this, "Could not connect to server, please try again later", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Log
                    Log.d("MapyClass2", "connection error");

                    // Remove Observer
                    SS.RemoveObserver(this);
                    SS.Close();
                    CloseConnection();
                }

                public void OnIdiling() {
                    // Notify User
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(Mapy2.this, "Please wait, trying to connect to the server", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }
}