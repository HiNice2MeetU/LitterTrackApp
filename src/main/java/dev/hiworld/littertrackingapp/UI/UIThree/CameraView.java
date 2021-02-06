package dev.hiworld.littertrackingapp.UI.UIThree;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
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
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import dev.hiworld.littertrackingapp.R;
//import dev.hiworld.littertrackingapp.UI.UIThree.HomeFragments.MappyFragDirections;
import dev.hiworld.littertrackingapp.UI.UITwo.EventMeta2;
import dev.hiworld.littertrackingapp.Utility.BMPCache;
import dev.hiworld.littertrackingapp.Utility.UtilityManager;

public class CameraView extends AppCompatActivity {
    // Globals
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private ProcessCameraProvider cameraProvider;
    private BMPCache BitCase;

    // Permission Handeling
    private int REQUEST_CODE_PRIVS = 101;
    private String[] REQUIRED_PRIVS = {"android.permission.CAMERA"};
    private UtilityManager PrivM = new UtilityManager(REQUIRED_PRIVS, REQUEST_CODE_PRIVS);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Hide Status bar
        getSupportActionBar().hide();


        // Move to dedicated nested graph
//        MoveToCamnav();
    }

//    private void MoveToCamnav() {
//        // Get Nav Controller
//        NavController NavControl = Navigation.findNavController(findViewById(R.id.CamFrag));
//
//        // Navigate
//        NavDirections action = MappyFragDirections.actionMappyFragToCamnav();
//        NavControl.navigate(action);
//    }
}
