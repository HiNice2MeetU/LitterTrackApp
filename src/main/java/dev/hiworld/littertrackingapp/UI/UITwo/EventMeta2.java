package dev.hiworld.littertrackingapp.UI.UITwo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Arrays;

import dev.hiworld.littertrackingapp.Network.Event;
import dev.hiworld.littertrackingapp.Network.NetworkOne.ServerScheduler;
import dev.hiworld.littertrackingapp.Network.NetworkOne.ServerTransport;
import dev.hiworld.littertrackingapp.Network.NetworkOne.SocketResultSet;
import dev.hiworld.littertrackingapp.R;
import dev.hiworld.littertrackingapp.UI.UIThree.CameraView;
import dev.hiworld.littertrackingapp.Utility.BMPCache;
import dev.hiworld.littertrackingapp.Utility.PrivOBJ;
import dev.hiworld.littertrackingapp.Utility.UtilityManager;

public class EventMeta2 extends AppCompatActivity implements View.OnClickListener {
    // Globals
    private ServerTransport ST = ServerTransport.getInstance();
    static protected ArrayList ConnectionDetails = new ArrayList(Arrays.asList("192.168.6.133", 2048));
    //private ServerScheduler ServScheduler = new ServerScheduler();
    private int ConnectionAttempts = 0;
    private FusedLocationProviderClient FuseLoc;
    private LatLng Loc;
    private UtilityManager UM = new UtilityManager();
    private PrivOBJ[] PrivOBJArray = {new PrivOBJ("android.permission.WRITE_EXTERNAL_STORAGE", 69)};
    private Bitmap Img;
    private ServerScheduler SS = new ServerScheduler();
    private boolean IsGallery = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Default Init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_meta2);


        // Log
        //Log.d("EventMetaClass2", "ID = " + String.valueOf(ConnectID));

        // Get UI Elements
        Button Nay = findViewById(R.id.Nay);
        Button Yay = findViewById(R.id.Yay);
        Button LocRefresh = findViewById(R.id.LocRefresh);
        ImageView ImgV = findViewById(R.id.ImgDisplay);
        ToggleButton SaveGallery = findViewById(R.id.ToGallery);

        // Toggle button listner
        SaveGallery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                IsGallery = isChecked;
                //Log
                Log.d("EventMetaClass2", "IsGallery = " + String.valueOf(IsGallery));
            }
        });

        // Add Listeners
        Nay.setOnClickListener(this);
        Yay.setOnClickListener(this);
        LocRefresh.setOnClickListener(this);

        // Get image
        BMPCache BitCase = new BMPCache();
        Img = BitCase.RetrieveBitmap("TempIMG");

        // Set image
        if (Img != null && ImgV!=null) {
            ImgV.setImageBitmap(Img);
        }

        // Set Location
        FuseLoc = LocationServices.getFusedLocationProviderClient(this);
        GetLocation();
    }

    private void GetLocation(){

        // Update fused location
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Create update call back
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
            }
        };

        // Acutally execute
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, mLocationCallback, null);

        // Get Last location
        FuseLoc.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    // If everything goes well set Text to lat and long
                    LatLng Location = new LatLng(location.getLatitude(), location.getLongitude());
                    UpdateScreenLoc(Location);
                    Loc = Location;
                } else {
                    Log.e("EventMetaClass2", "Location == null");
                }
            }
        });
    }

    private void UpdateScreenLoc(LatLng Location){
        // Get Ui location
        TextView LatTx = findViewById(R.id.Lat2);
        TextView LngTx = findViewById(R.id.Lng2);

        // Set Text
        LatTx.setText(String.valueOf(Location.latitude));
        LngTx.setText(String.valueOf(Location.longitude));
    }

    public void CloseConnection(){
        ST.Execute(new SocketResultSet("Disconnect"));
        ST.Execute(new SocketResultSet("Close"));
    }

    // Click
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.Nay:
                // If user declines image move back to camera view
                Intent i = new Intent(this, CameraView.class);

                // Log
                Log.d("EventMetaClass2", "User declined pic");

                // Actually do it
                startActivity(i);
                break;
            case R.id.Yay:

                break;
            case R.id.LocRefresh:
                // If user wants to refresh location update current location
                GetLocation();

                // Notify user
                Toast.makeText(EventMeta2.this, "Location updated", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // New Connection Method
    public void NewConnect(){

    }

    public void OldConnection(){
        // Check if location == null
        if (Loc != null){
            // Connect to server
            int ConnectID = ST.Execute(new SocketResultSet("Connect", ConnectionDetails));

            // Add observer to that
            SS.AddObserver(ConnectID, new ServerScheduler.ResultListener() {
                // When there is a acknolegment statement
                public void OnSucess(SocketResultSet Msg) {
                    // Log
                    Log.d("EventMetaClass2", "Connect was a sucess");
                    //Toast.makeText(this, "Could not connect to the sever, try again later", Toast.LENGTH_SHORT).show();

                    // Remove Observer
                    SS.RemoveObserver(this);

                    // Log
                    Log.d("EventMetaClass2", Loc.toString());

                    // Send Event
                    if (Img != null) {
                        // Check if loc == null
                        if (Loc != null) {
                            // Scale Bitmap
                            //Bitmap NewImg = UtilityManager.ScaleBmp(Img, 200,200);

                            // Execute send event
                            int SendEventID = ST.Execute(new SocketResultSet("SendEvent", new ArrayList(Arrays.asList(new Event(Loc.latitude, Loc.longitude, "A")))));
                            //int SendEventID = ST.Execute(new SocketResultSet("SendEvent", new ArrayList(Arrays.asList(new Event(Loc.latitude, Loc.longitude, UM.ToBase64(NewImg))))));
                            //int SendEventID = ST.Execute(new SocketResultSet("SendEvent", new ArrayList(Arrays.asList(new Event(Loc.latitude, Loc.longitude, UM.ToBase64(NewImg))))));
                            //int SendEventID = ST.Execute(new SocketResultSet("SendEvent", new ArrayList(Arrays.asList(new Event(20, 20, UM.ToBase64(NewImg))))));

                            // Save image to gallery if selected
                            if (IsGallery == true){
                                //SaveImgToGallery(Img, CurrentTime.toString(), "Picture taken at " + Loc.toString());
                                //DefaultSaveImg();

                                //Log
                                //Log.d("EventMetaClass2", "Saving pic");
                            }

                            // Attach Listener
                            SS.AddObserver(SendEventID, new ServerScheduler.ResultListener() {
                                // If could sucessfully send info
                                public void OnSucess(SocketResultSet Msg) {
                                    // Remove Observer
                                    SS.RemoveObserver(this);

                                    // Close Connections
                                    CloseConnection();
                                    SS.Close();
                                }
                                // Could not send info
                                public void OnError() {
                                    // Remove Observer
                                    SS.RemoveObserver(this);

                                    // Notify User
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(EventMeta2.this, "Could not send infomation, please try again later", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    // Close Connections
                                    CloseConnection();
                                    SS.Close();
                                }
                                // Taking its time
                                public void OnIdiling() {

                                    // Notify User
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(EventMeta2.this, "Trying to send infomation, please wait", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });

                            // Move back to main class
                            Intent l = new Intent(EventMeta2.this, MainActivity.class);
                            startActivity(l);
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(EventMeta2.this, "Could not find location, try refreshing location", Toast.LENGTH_SHORT).show();
                                }
                            });

                            // Close Connections
                            CloseConnection();
                            SS.Close();
                        }
                    } else {
                        // Close Connections
                        CloseConnection();
                        SS.Close();
                    }
                }

                // When no return is reached
                public void OnError() {
                    // Notify User
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(EventMeta2.this, "Could not connect to server, please try again later", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Remove Observer
                    SS.RemoveObserver(this);

                    // Close
                    SS.Close();
                }

                // When waiting more than idle threshold
                public void OnIdiling() {
                    // Log
                    Log.d("EventMetaClass2", "Connect is idling");

                    // Notify User
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(EventMeta2.this, "Please wait, trying to connect to the server", Toast.LENGTH_SHORT).show();
                        }
                    });

                    //SS.RemoveObserver(this);
                }
            });
        }
    }
}