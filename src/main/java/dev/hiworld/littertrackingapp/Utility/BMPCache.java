package dev.hiworld.littertrackingapp.Utility;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

public class BMPCache {
    private static BMPCache Instance;
    private LruCache<Object, Object> Lru;

    public BMPCache() {
        Lru = new LruCache<Object, Object>(1024);
    }

    public static BMPCache getInstance() {

        if (Instance == null) {
            Instance = new BMPCache();
        }
        return Instance;

    }

    public LruCache<Object, Object> getLru() {
        return Lru;
    }

    public void SaveBitmap(String key, Bitmap bitmap){
        try {
            BMPCache.getInstance().getLru().put(key, bitmap);
            Log.d("BitmapCache", "Cache (Saved): "+Lru.toString());
        } catch (Exception e){
            Log.d("BitmapCache", "Something has gone terribly wrong saving");
        }
    }

    public Bitmap RetrieveBitmap(String key){

        try {
            Bitmap bitmap = (Bitmap) BMPCache.getInstance().getLru().get(key);
            Log.d("BitmapCache", "Cache (Returned): "+Lru.toString());
            return bitmap;
        } catch (Exception e){
            Log.d("BitmapCache", "Something has gone terribly wrong retrieving");
            return null;
        }

    }
}
