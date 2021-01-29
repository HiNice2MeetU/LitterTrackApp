package dev.hiworld.littertrackingapp.UI.UIThree.CameraFragments;

import android.location.Location;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import dev.hiworld.littertrackingapp.R;
import dev.hiworld.littertrackingapp.Utility.BMPCache;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CamAcceptance#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CamAcceptance extends Fragment {

    private BMPCache BitCase = new BMPCache();
    private FusedLocationProviderClient fusedLocationClient;
    private Location Loc;

    public CamAcceptance() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View InflatedView = inflater.inflate(R.layout.fragment_cam_acceptance, container, false);

        // Set img
        ImageView ImgDisplay = InflatedView.findViewById(R.id.ImgPreview);
        ImgDisplay.setImageBitmap(BitCase.RetrieveBitmap("TempIMG"));

        // Get Elements
        FloatingActionButton Accept = InflatedView.findViewById(R.id.Accept);
        FloatingActionButton Decline = InflatedView.findViewById(R.id.Decline);
        TextView LocMsg = InflatedView.findViewById(R.id.LocNotFound);

        // Get Provider
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Configure Buttons
        Accept.setEnabled(false);

        // Set Callbacks
        Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send img to server
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

        // Get Location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        // Log
                        Log.d("CameraFrag", "Got Location: " + String.valueOf(location));
                        if (location != null) {
                            // If loaction is ok dissapear msg
                            LocMsg.setVisibility(View.GONE);

                            // update location
                            Loc = location;

                            // Enable accept button
                            Accept.setEnabled(true);
                        }
                    }
                });

        return InflatedView;
    }

    public void ProcessImg() {

    }
}