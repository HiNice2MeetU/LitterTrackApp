package dev.hiworld.littertrackingapp.UI.UIThree.CameraFragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import dev.hiworld.littertrackingapp.R;
import dev.hiworld.littertrackingapp.UI.UITwo.EventMeta2;
import dev.hiworld.littertrackingapp.Utility.BMPCache;
import dev.hiworld.littertrackingapp.Utility.UtilityManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import dev.hiworld.littertrackingapp.R;


public class CameraFrag extends Fragment implements View.OnClickListener {

    // Globals
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private ProcessCameraProvider cameraProvider;
    private BMPCache BitCase;
    private Context ConA;

    // Permission Handeling
    private int REQUEST_CODE_PRIVS = 101;
    private String[] REQUIRED_PRIVS = {"android.permission.CAMERA"};
    private UtilityManager PrivM = new UtilityManager(REQUIRED_PRIVS, REQUEST_CODE_PRIVS);

    public CameraFrag() {
        // Required empty public constructor
    }

    public static CameraFrag newInstance(String param1, String param2) {
        CameraFrag fragment = new CameraFrag();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ConA = (Context)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // On create magic
        super.onCreate(savedInstanceState);
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        // Config Camera
        Log.d("CameraGUI", "Value of CamProvider == " + String.valueOf(cameraProvider));
        if (cameraProvider != null) {
            imageCapture = new ImageCapture.Builder().setTargetRotation(previewView.getDisplay().getRotation()).setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build();
            CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

            //cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageCapture, imageAnalysis, preview);

            // Connect PreviewView to camera
            Preview preview = new Preview.Builder().build();

            preview.setSurfaceProvider(previewView.getSurfaceProvider());

            Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageCapture, preview);
        } else {
            Log.d("CameraGUI", "Loading?");
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // Handle if user denies privs
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Priv is granted
                //finish();
                //startActivity(getIntent());
            }  else {
                // Priv is not granted
                Toast.makeText(ConA, "Permissions has not been granted", Toast.LENGTH_SHORT).show();

                //Log.d("Main", "Sucessfully opened Camera App");
                startActivity(PrivM.MoveBackToHome(ConA));
            }
            return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

    private void TakePicture(){
        // Specify Output to...
        //ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(new File("/DCIM/Camera/img.jpg")).build();

        // Take the pic
        Toast ErrorToast = Toast.makeText(ConA, "Something went wrong!", Toast.LENGTH_SHORT);
        Toast SucessToast = Toast.makeText(ConA, "Sucessfully took the photo", Toast.LENGTH_SHORT);
        Intent i = new Intent(ConA, EventMeta2.class);
        imageCapture.takePicture(Executors.newSingleThreadExecutor(),
                new ImageCapture.OnImageCapturedCallback() {

                    public void onCaptureSuccess (ImageProxy imageProxy) {
                        // If image is captured
                        SucessToast.show();
                        Log.d("CameraGUI", "Image taken good");

                        // Convert img
                        //Image TakenImage = imageProxy.getImage();


                        // Save img
                        Log.d("CameraGUI", String.valueOf(imageProxy.getFormat()));

                        // Make intent

                        //i.putExtra("img",image);

                        //MediaStore.Images.Media.insertImage(getContentResolver(),PrivM.imageProxyToBitmap(imageProxy) , "Test", "TestyTest");

                        // Store image in cache
                        //BitCase.Open();
                        BitCase.SaveBitmap("TempIMG", UtilityManager.CompressBitmap(PrivM.ToBitmap(imageProxy), 10));

                        imageProxy.close();
                        startActivity(i);
                    }

                    @Override
                    public void onError (ImageCaptureException exception) {
                        ErrorToast.show();
                        Log.d("CameraGUI", "Image taken bad");
                        Log.d("CameraGUI", exception.toString());
                        //startActivity(MoveBackToHome);
                    }
                });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.TakeIMG: {
                // Handle Camera Button Click
                TakePicture();
                break;
            } case R.id.BackButton: {
                startActivity(PrivM.MoveBackToHome(ConA));
                break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View InflatedView = inflater.inflate(R.layout.fragment_camera, container, false);

        // Init Button
        ImageButton TakePicB = (ImageButton)InflatedView.findViewById(R.id.TakeIMG);
        TakePicB.setOnClickListener(this);

        ImageButton GoBackB = (ImageButton)InflatedView.findViewById(R.id.BackButton);
        GoBackB.setOnClickListener(this);

        // Init Camera
        previewView =InflatedView.findViewById(R.id.previewView);
        //MoveBackToHome = new Intent(this, MainActivity.class);
        BitCase = new BMPCache();

        if (PrivM.CheckPrivs(ConA)) {
            // If all privs been met
            cameraProviderFuture = ProcessCameraProvider.getInstance(ConA);
            cameraProviderFuture.addListener(() -> {
                try {
                    // If Camera is avaliable
                    cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }, ContextCompat.getMainExecutor(ConA));
        } else {
            // If all privs hasn't been met
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                // Explain to user this permission
                Toast.makeText(ConA, "The camera permission is needed to use this feature", Toast.LENGTH_SHORT).show();
            }

            // Request Privs
            ActivityCompat.requestPermissions((Activity)ConA, REQUIRED_PRIVS, REQUEST_CODE_PRIVS);
        }

        return InflatedView;
    }

}