package dev.hiworld.littertrackingapp.Utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import dev.hiworld.littertrackingapp.Network.Event;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;

import dev.hiworld.littertrackingapp.R;

public class TrashConA implements GoogleMap.InfoWindowAdapter {
    // Make init vars
    private View TCon;
    private Gson gson = new Gson();

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

        // Get Event from snippet
        Event ImgEvent = gson.fromJson(marker.getSnippet(), Event.class);

        // set the user text
        TextView NameDisplay = TCon.findViewById(R.id.User);
        NameDisplay.setText(ImgEvent.getDisplayName());

        // Check bmp isnt null
        String Bmp = ImgEvent.getBmp();
        if (Bmp != null) {
            // Set image view
            ((ImageView) TCon.findViewById(R.id.Img)).setImageBitmap(UtilityManager.FromBase64(Bmp));
        }

    }
}