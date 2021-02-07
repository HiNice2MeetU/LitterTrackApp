package dev.hiworld.littertrackingapp.Utility;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

public class PrivUtility {

    public static boolean CheckPrivs(Context context, String[] REQUIRED_PRIVS){
        // Check Privs
        for (String Perm : REQUIRED_PRIVS){
            // Loop through Privs
            if (ContextCompat.checkSelfPermission(context, Perm) != PackageManager.PERMISSION_GRANTED){
                // If priv is not met
                return false;
            }
        }
        return true;
    }

    public static boolean CheckGrantResults(int[] GrantResults){
        for (int Current:GrantResults) {
            if (Current == PackageManager.PERMISSION_GRANTED) {

            } else {
                return false;
            }
        }
        return true;
    }
}
