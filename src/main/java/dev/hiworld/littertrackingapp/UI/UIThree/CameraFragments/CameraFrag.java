package dev.hiworld.littertrackingapp.UI.UIThree.CameraFragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import dev.hiworld.littertrackingapp.R;
import dev.hiworld.littertrackingapp.Utility.BMPCache;
import dev.hiworld.littertrackingapp.Utility.UtilityManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.ViewGroup;


public class CameraFrag extends Fragment {

    // Globals
    private View InflatedView;
    private ListenableFuture<ProcessCameraProvider> CameraProviderFuture;
    private BMPCache BitCase = new BMPCache();
    private int PictureQuality = 50;
    private int PictureSizeX = 100;
    private int PictureSizeY = 100;
    private ImageCapture imageCapture;
    private String[] Privs = { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,  Manifest.permission.ACCESS_FINE_LOCATION};

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

        //Log.d("CameraFrag", "Help");

        // Init priv and cam
        InitPriv();

        // Add Listener to take pic button
        ImageButton PhotoButton = InflatedView.findViewById(R.id.TakeIMG);
        PhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoButton.setEnabled(false);
                TakePhoto();

            }
        });

        // Return View
        return InflatedView;
    }

    // Check privs then init cam
    public void InitPriv() {

        if (CheckPrivs(Privs) == true) {
            // Init Camera
            InitCamera();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), "Manifest.permission.CAMERA") == true) {
            // Notify User
            Toast.makeText(getActivity(), getString(R.string.info_permission_camera), Toast.LENGTH_SHORT);
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), "Manifest.permission.WRITE_EXTERNAL_STORAGE") == true) {
            // Notify User
            Toast.makeText(getActivity(), getString(R.string.info_permission_external_storage), Toast.LENGTH_SHORT);
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), "Manifest.permission.ACCESS_FINE_LOCATION") == true) {
            // Notify User
            Toast.makeText(getActivity(), getString(R.string.info_permission_location), Toast.LENGTH_SHORT);
        } else {
            // If Privs aren't givient
            requestPermissions(Privs, 69);
        }

    }

    private boolean CheckPrivs(String[] Privs) {
        for (String Current:Privs){
            Log.d("CameraFrag", "Checking: " + Current);
            if (ContextCompat.checkSelfPermission(getActivity(), Current) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
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

        // Set image capture
        imageCapture =
                new ImageCapture.Builder()
                        .setTargetRotation(InflatedView.getDisplay().getRotation())
                        .build();


        // Bind
        Camera Camera = CameraProvider.bindToLifecycle((LifecycleOwner)this, CamSelector,imageCapture, preview);
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
                if (CheckGrantResults(grantResults)) {
                    // If priv is granted
                    InitPriv();

                    // Log
                    Log.d("CameraFrag", "Priv is granted");
                    Log.d("CameraFrag", "GrantResults = " + Arrays.toString(grantResults));

                    // Restart Fragment
//                    NavDirections action = CameraFragDirections.actionCameraFragSelf();
//                    Navigation.findNavController(InflatedView.findViewById(R.id.previewView)).navigate(action);
                } else {
                    // If priv isnt granted go back to map
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

    public void TakePhoto() {
        imageCapture.takePicture(Executors.newSingleThreadExecutor(),
                new ImageCapture.OnImageCapturedCallback() {
                    public void onCaptureSuccess (ImageProxy imageProxy) {
                        // Log
                        Log.d("CameraGUI", "Image taken good");
                        Log.d("CameraGUI", String.valueOf(imageProxy.getFormat()));

                        // Cache
                        BitCase.SaveObject("TempIMG", UtilityManager.ToBase64(UtilityManager.ScaleBmp(UtilityManager.ToBitmap(imageProxy),PictureSizeX,PictureSizeY), PictureQuality));

                        // Notify User
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                // Toast
                                Toast.makeText(getActivity(), getString(R.string.camera_sucess), Toast.LENGTH_SHORT).show();

                                // Go to cam acceptance
                                NavDirections action = CameraFragDirections.actionCameraFragToCamAcceptance();
                                Navigation.findNavController(getActivity(), R.id.previewView).navigate(action);
                            }
                        });
                    }

                    @Override
                    public void onError (ImageCaptureException exception) {
                        // Log
                        Log.d("CameraGUI", "Image failed");

                        // Notify User
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getActivity(), getString(R.string.camera_failiure), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }
}