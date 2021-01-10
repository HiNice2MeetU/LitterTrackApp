package dev.hiworld.littertrackingapp.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Base64;
import android.util.Log;

import androidx.camera.core.ImageProxy;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import dev.hiworld.littertrackingapp.UI.MainActivity;
import kotlin.jvm.internal.Intrinsics;

public class UtilityManager {

    // Globals
    private int REQUEST_CODE_PRIVS = 101;
    private String[] REQUIRED_PRIVS;
    private Intent MoveBackToHome;
    private ArrayList<Integer> Users = new ArrayList<Integer>();
    private static Random rand = new Random();
    private static LinkedList<Integer> IdList = new LinkedList<Integer>();

    public UtilityManager(String[] Permissions, int CodePriv){
        REQUIRED_PRIVS = Permissions;
        REQUEST_CODE_PRIVS = CodePriv;
    }

    public UtilityManager(){
    }

    public boolean CheckPrivs(Context context){
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

    public void RequestPrivs(Context con, Activity an) {
        ActivityCompat.requestPermissions(an, REQUIRED_PRIVS, REQUEST_CODE_PRIVS);
    }

    public static final Bitmap ToBitmap(ImageProxy image) {
        // Converted from kotlin bytecode
        ImageProxy.PlaneProxy planeProxy = image.getPlanes()[0];
        Intrinsics.checkNotNullExpressionValue(planeProxy, "planeProxy");
        ByteBuffer var10000 = planeProxy.getBuffer();
        Intrinsics.checkNotNullExpressionValue(var10000, "planeProxy.buffer");
        ByteBuffer buffer = var10000;
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        Bitmap var5 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        Intrinsics.checkNotNullExpressionValue(var5, "BitmapFactory.decodeByte…ray(bytes, 0, bytes.size)");
        return var5;
    }

    // Encode to Base 64 String
    public static String ToBase64(Bitmap Bmp) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // Decode frome Base 64 String to Bmp
    public static Bitmap FromBase64(String In){
        byte[] decodedString = Base64.decode(In, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public int getREQUEST_CODE_PRIVS() {
        return REQUEST_CODE_PRIVS;
    }

    public void setREQUEST_CODE_PRIVS(int REQUEST_CODE_PRIVS) {
        this.REQUEST_CODE_PRIVS = REQUEST_CODE_PRIVS;
    }

    public String[] getREQUIRED_PRIVS() {
        return REQUIRED_PRIVS;
    }

    public void setPermissions(String[] permissions) {
        REQUIRED_PRIVS = permissions;
    }

    public Intent MoveBackToHome(Context con){
        MoveBackToHome = new Intent(con, MainActivity.class);
        return MoveBackToHome;
    }

    public static int GenorateID(){
        // Genorate a rand ind
        int Id = rand.nextInt(1000);
        if (IdList.contains(Id)){
            //System.out.println("Reiterating UID");
            Log.d("UtilityManager", "Reiterating UID");
            return GenorateID();
        } else {
            IdList.addLast(Id);
            return Id;
        }
    }

    public static Bitmap CompressBitmap(Bitmap Bmp, int Quality){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Bmp.compress(Bitmap.CompressFormat.JPEG, Quality, out);
        return BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
    }

    public static Bitmap ScaleBmp(Bitmap originalImage, int width, int height) {
        Bitmap background = Bitmap.createBitmap((int)width, (int)height, Bitmap.Config.ARGB_8888);

        float originalWidth = originalImage.getWidth();
        float originalHeight = originalImage.getHeight();

        Canvas canvas = new Canvas(background);

        float scale = width / originalWidth;

        float xTranslation = 0.0f;
        float yTranslation = (height - originalHeight * scale) / 2.0f;

        Matrix transformation = new Matrix();
        transformation.postTranslate(xTranslation, yTranslation);
        transformation.preScale(scale, scale);

        Paint paint = new Paint();
        paint.setFilterBitmap(true);

        canvas.drawBitmap(originalImage, transformation, paint);

        return background;
    }
}
