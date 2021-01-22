package dev.hiworld.littertrackingapp.UI.UIThree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import dev.hiworld.littertrackingapp.R;
import dev.hiworld.littertrackingapp.UI.UIThree.HomeFragments.TestFragmentDirections;

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

                // Get nav
                NavController NavControl = Navigation.findNavController(findViewById(R.id.Frag));

                String CurrentDest = NavControl.getCurrentDestination().getLabel().toString();

                // Switch Statement to determine what to do
                switch (ItemID) {
                    case "Camera":
                        // If Camera is selected
                        break;
                    case "Map":
                        // If Map is selected
                        break;
                    case "Settings":
                        // If Settings is selected navigate to settings
                        if (!CurrentDest.equals("fragment_test2")) {
                            NavDirections Action = TestFragmentDirections.actionTestFragmentToTestFragment2();
                            NavControl.navigate(Action);
                            Log.d("HomeActivity", "on destination: " + CurrentDest);
                        } else {
                            Log.e("HomeActivity", "Already on destination: " + CurrentDest);
                        }
                        break;
                }

                // Log
                Log.d("HomeActivity", "Bottom Bar Selected: " + ItemID);

                // Return
                return true;
            }
        });
    }
}
