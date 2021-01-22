package dev.hiworld.littertrackingapp.UI.UIThree.CameraFragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import dev.hiworld.littertrackingapp.R;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.ViewGroup;


public class CameraFrag extends Fragment {

    // Globals
    private View InflatedView;
    private ListenableFuture<ProcessCameraProvider> CameraProviderFuture;

    public CameraFrag(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // On create magic
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        InflatedView = inflater.inflate(R.layout.fragment_camera, container, false);

        // Init priv and cam
        InitPriv();

        // Return View
        return InflatedView;
    }

    // Check privs then init cam
    public void InitPriv() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Init Camera
            InitCamera();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), "Manifest.permission.CAMERA") == true) {
            // Notify User
            Toast.makeText(getActivity(), getString(R.string.info_permission_camera), Toast.LENGTH_SHORT);
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), "Manifest.permission.WRITE_EXTERNAL_STORAGE") == true) {
            // Notify User
            Toast.makeText(getActivity(), getString(R.string.info_permission_external_storage), Toast.LENGTH_SHORT);
        } else {
            // If Privs aren't givient
            requestPermissions(new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 69);
        }

    }

    public void BindPreview(ProcessCameraProvider CameraProvider) {

        // Get preview view
        PreviewView PreviewV = InflatedView.findViewById(R.id.previewView);

        // Create Preview
        Preview preview = new Preview.Builder().build();

        // Select the back camera
        CameraSelector CamSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

        // Set surface provider
        preview.setSurfaceProvider(PreviewV.getSurfaceProvider());

        // Bind
        Camera Camera = CameraProvider.bindToLifecycle((LifecycleOwner)this, CamSelector, preview);
    }


    // Initialize Cam
    public void InitCamera() {
        // Get Camera Provider
        CameraProviderFuture = ProcessCameraProvider.getInstance(getActivity());

        // Check for availability
        CameraProviderFuture.addListener(() -> {
            try {
                // Get Provider
                ProcessCameraProvider CamProvider = CameraProviderFuture.get();

                // Unbind
                CamProvider.unbindAll();

                // Bind Preview
                BindPreview(CamProvider);

            } catch (ExecutionException | InterruptedException e) {
                // If any errors occur
                Log.e("CameraFrag", "Something Went Wrong");
            }
        },ContextCompat.getMainExecutor(getActivity()));
    }

    // On permission result
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 69:
                // Request for write external and cam
                if (grantResults.length > 1 && CheckGrantResults(grantResults)) {
                    // If priv is granted
                    InitCamera();

                    // Log
                    Log.d("CameraFrag", "Priv is granted");
                    Log.d("CameraFrag", "GrantResults = " + Arrays.toString(grantResults));
                } else {
                    // If priv isnt granted
                    NavDirections action = CameraFragDirections.actionCameraFragToHomeActvity();
                    Navigation.findNavController(InflatedView.findViewById(R.id.previewView)).navigate(action);


                    // Log
                    Log.d("CameraFrag", "Priv is not granted");
                    Log.d("CameraFrag", "GrantResults = " + grantResults.toString());
                }

                return;
        }
    }

    public boolean CheckGrantResults(int[] GrantResults){
        for (int Current:GrantResults) {
            if (Current == PackageManager.PERMISSION_GRANTED) {

            } else {
                return false;
            }
        }
        return true;
    }
}