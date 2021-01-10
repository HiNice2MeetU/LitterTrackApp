package dev.hiworld.littertrackingapp.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PrivOBJ {
    private String Priv;
    private int Code;

    public PrivOBJ(String priv, int code) {
        Priv = priv;
        Code = code;
    }

    public String getPriv() {
        return Priv;
    }

    public void setPriv(String priv) {
        Priv = priv;
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }

    public String[] toArray(String target){
        return new String[]{target};
    }

    public boolean CheckAccepted(int requestCode, String[] permissions, int[] grantResults){
        return ((requestCode == Code) && (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) == true;
    }

    public boolean CheckBeenAccepted(Context con){
        return ContextCompat.checkSelfPermission(con, Priv) == PackageManager.PERMISSION_GRANTED;
    }

    public void RequestPriv(Activity con){
        ActivityCompat.requestPermissions(con, new String[]{Priv},Code);
    }

}
