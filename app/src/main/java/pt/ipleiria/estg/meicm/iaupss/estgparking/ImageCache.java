package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.LruCache;
import android.util.Log;

import java.io.File;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Created by francisco on 11-12-2014.
 */
public class ImageCache {

    private static final String TAG = "IMAGE_CACHE";
    private static final String CACHE_FOLDER = Environment.getDataDirectory()
            + "/data/pt.ipleiria.estg.meicm.iaupss.estgparking/Images/";
    private static final int MAX_SIZE = 64 * 1024 * 1024; // 64 MiB

    private LruCache<String, Bitmap> imgCache;

    public ImageCache() {
        this.initializeCache();
    }

    public void initializeCache() {

        this.imgCache = new LruCache<String, Bitmap>(MAX_SIZE) {

            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }

            @Override
            protected void entryRemoved(boolean evicted, String key,
                                        Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
            }

        };

    }

    public void addElement(String key, Bitmap value) {
        this.imgCache.put(key, value);
        Log.i(TAG, "Add Element: " + key + "(key)");
    }

    public Bitmap getElement(String key) {
		/*
		 * value = this.newCache.get(key); this.autoRemoveContext = false;
		 * this.newCache.remove(key); this.newCache.put(key, value);
		 */

        return this.imgCache.get(key);
    }

    public boolean containsElement(String key) {
        Bitmap element = this.imgCache.get(key);
        return (element != null);
    }

    public void removeImage(String imageName) {

        File imageFile = new File(CACHE_FOLDER + generateChecksum(imageName));
        imageFile.delete();

    }

    public String generateChecksum(String value) {
        Checksum checksum = new CRC32();
        byte[] byteArray = value.getBytes();

        checksum.update(byteArray, 0, byteArray.length);
        long checksumValue = checksum.getValue();

        String s = String.valueOf(checksumValue);
        Log.i(TAG, "Image Name Checksum is " + s);

        return s;
    }
}
