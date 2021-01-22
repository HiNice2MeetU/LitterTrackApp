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
import androidx.camera.core.CameraX;
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
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import dev.hiworld.littertrackingapp.R;


public class CameraFrag extends Fragment implements View.OnClickListener {

    public CameraFrag() {
        // Required empty public constructor
    }

    public static CameraFrag newInstance(String param1, String param2) {
        CameraFrag fragment = new CameraFrag();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // On create magic
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View InflatedView = inflater.inflate(R.layout.fragment_camera, container, false);

        // Return View
        return InflatedView;
    }

}