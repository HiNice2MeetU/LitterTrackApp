package dev.hiworld.littertrackingapp.UI.UIThree.CameraFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import dev.hiworld.littertrackingapp.R;
import dev.hiworld.littertrackingapp.Utility.BMPCache;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CamAcceptance#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CamAcceptance extends Fragment {

    private BMPCache BitCase = new BMPCache();

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



        return InflatedView;
    }
}