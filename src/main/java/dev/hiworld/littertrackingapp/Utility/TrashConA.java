package dev.hiworld.littertrackingapp.Utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import dev.hiworld.littertrackingapp.R;

public class TrashConA implements GoogleMap.InfoWindowAdapter {
    // Make init vars
    private View TCon;
    private Bitmap Bmp;

    // Constructor
    public TrashConA(Context Con) {
        TCon = LayoutInflater.from(Con).inflate(R.layout.custom_info_window, null);
        Log.d("MapyClass2", "TCon = " + TCon.toString());
    }

    public View getInfoWindow(Marker marker) {
        //return TCon;
        return null;
    }

    // Return content
    public View getInfoContents(Marker marker) {
        Render(marker);
        return TCon;
        //return null;
    }

    public void Render(Marker marker){
        // Get the position of marker
        LatLng Pos = marker.getPosition();

        // Log
        Log.d("MapyClass2", "Pos = " + Pos.toString());

        // get the text views
        TextView Lat = (TextView) TCon.findViewById(R.id.Lat);
        TextView Lng = (TextView) TCon.findViewById(R.id.Lng);

        // set the text views
        Lat.setText(String.valueOf(Pos.latitude));
        Lng.setText(String.valueOf(Pos.longitude));

        // set the image view
        if (Bmp != null) {
            ((ImageView) TCon.findViewById(R.id.Img)).setImageBitmap(Bmp);
        }

    }

    // Getter and Setter
    public Bitmap getBmp() {
        return Bmp;
    }

    public void setBmp(Bitmap bmp) {
        Bmp = bmp;
    }
}