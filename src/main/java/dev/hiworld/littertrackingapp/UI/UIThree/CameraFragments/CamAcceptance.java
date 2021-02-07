package dev.hiworld.littertrackingapp.UI.UIThree.CameraFragments;

import android.location.Location;
import 	android.os.Handler;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Arrays;

import dev.hiworld.littertrackingapp.Network.Event;
import dev.hiworld.littertrackingapp.Network.*;
import dev.hiworld.littertrackingapp.R;
import dev.hiworld.littertrackingapp.Utility.BMPCache;
import dev.hiworld.littertrackingapp.Utility.UtilityManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CamAcceptance#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CamAcceptance extends Fragment implements MqttCallback {

    // Globals
    private BMPCache BitCase = new BMPCache();
    private FusedLocationProviderClient fusedLocationClient;
    private Location Loc;
    private final int LocWait = 1000; // 1 Second
    private boolean ShowedLocMsg = false;
    private String PublishID;
    private MQAsyncManager MQM = new MQAsyncManager();
    private String BmpBase;


    public CamAcceptance() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Img Base64
        BmpBase = (String)BitCase.RetrieveObject("TempIMG");

        // Inflate the layout for this fragment
        View InflatedView = inflater.inflate(R.layout.fragment_cam_acceptance, container, false);

        // Set img
        ImageView ImgDisplay = InflatedView.findViewById(R.id.ImgPreview);
        ImgDisplay.setImageBitmap(UtilityManager.FromBase64(BmpBase));

        // Get Elements
        FloatingActionButton Accept = InflatedView.findViewById(R.id.Accept);
        FloatingActionButton Decline = InflatedView.findViewById(R.id.Decline);

        // Get Provider
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Configure Buttons
        Accept.setEnabled(false);

        MQM.setFailed(true);

        // Get Account


        // Set Callbacks
        Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send img to server
                MQM.setFailed(false);
                Accept.setEnabled(false);
                ProcessImg();

            }
        });

        Decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Go back to camera frag
                NavDirections action = CamAcceptanceDirections.actionCamAcceptanceToCameraFrag();
                Navigation.findNavController(InflatedView.findViewById(R.id.Accept)).navigate(action);
            }
        });

        // Location Not Found
        Handler LocHandeler = new Handler();
        Runnable LocRunnable = new Runnable(){
            public void run() {
                // Notify User
                if (Loc==null) {
                    Toast.makeText(getActivity(), getString(R.string.info_unkown_location), Toast.LENGTH_SHORT).show();
                    ShowedLocMsg = true;
                    Log.d("CameraFrag", "Showing LocRunnable Msg");
                }

                Log.d("CameraFrag", "LocRunnable triggered");
            }
        };

        // Get Location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        // Log
                        Log.d("CameraFrag", "Got Location: " + String.valueOf(location));
                        if (location != null) {
                            // update location
                            Loc = location;

                            // Enable accept button
                            Accept.setEnabled(true);

                            // Show loc found toast if loc not found toast has been shown
                            if (ShowedLocMsg) {
                                Toast.makeText(getActivity(), getString(R.string.info_known_location), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });



        LocHandeler.postAtTime(LocRunnable, System.currentTimeMillis()+LocWait);
        LocHandeler.postDelayed(LocRunnable, LocWait);

        return InflatedView;
    }

    private void ProcessImg() {


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

        // Make command listener
        IMqttActionListener PublishListener = new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                // Log
                Log.d("CameraFrag", "MQM Action Sucesfull");
                //MQM.Next();

                // Go to home
                NavDirections action = CamAcceptanceDirections.actionCamAcceptanceToHomeActvity2();
                Navigation.findNavController(getView().findViewById(R.id.Accept)).navigate(action);
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.e("CameraFrag", "MQM Action failed");
                NotifyNetErr();
            }
        };

        // Make Display name
        String DisplayName = "Anonymous";

        // Get Account
        GoogleSignInAccount Account = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (Account != null) {
            DisplayName = Account.getDisplayName();
        }

        // Add commands
        MQM.Add(new MQMsg(new ArrayList<Object>(Arrays.asList("tcp://192.168.6.133:1883", this, false)), "Connect"),MQListener);
        MQM.Add(new MQMsg(new ArrayList<Object>(Arrays.asList()), "Subscribe"), MQListener);
        PublishID = MQM.Add(new MQMsg(new ArrayList<Object>(Arrays.asList(new Event(Loc.getLatitude(),Loc.getLongitude(),BmpBase,DisplayName))), "AddRow"), PublishListener);
        //MQM.Add(new MQMsg(new ArrayList<Object>(Arrays.asList(new Event(100.0,200.0,BmpBase))), "AddRow"), PublishListener);
        MQM.Next();

        // Toast
        Toast.makeText(getActivity(), getString(R.string.info_contacting_server), Toast.LENGTH_SHORT).show();
    }

    private void NotifyNetErr() {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                // Toast
                Toast.makeText(getActivity(), getString(R.string.info_connection_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

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
        Log.d("MQAsyncClient", "Msg Arrived: " + RawMsg);

        // Check Formatted Msg != null
        if (FormattedMsg != null) {
            Log.d("MQAsyncClient", "Formatted Msg Arrived: " + FormattedMsg.toString());
        } else {
            Log.d("MQAsyncClient", "Formatted Msg Failed: " + RawMsg);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

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
                NotifyNetErr();
                //CamAcceptance.super.onStop();
            }
        };

        if (!MQM.isFailed()) {
            MQM.Add(new MQMsg("Disconnect"), MQDisconnect);
            MQM.Next();
        }

        CamAcceptance.super.onStop();
    }
}