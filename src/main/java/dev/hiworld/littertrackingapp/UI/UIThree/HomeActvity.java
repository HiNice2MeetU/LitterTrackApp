package dev.hiworld.littertrackingapp.UI.UIThree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import dev.hiworld.littertrackingapp.R;

public class HomeActvity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_actvity);

        // Get Bottom Bar
        BottomNavigationView BottomBar = findViewById(R.id.BottomNavi);

        // Set Bottom Bar Callback
        BottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Switch statement to determine menue item
                String ItemID = (getResources().getResourceName(item.getItemId()).split("\\/"))[1];

                // Log
                Log.d("HomeActivity", "Bottom Bar Selected: " + ItemID);

                // Return
                return true;
            }
        });
    }
}