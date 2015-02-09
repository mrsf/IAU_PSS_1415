package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

public class ImageCache {

    private static final String TAG = "IMAGE_CACHE";
    private static final int MAX_SIZE = 16 * 1024 * 1024; // 16 MiB
    /*private static final String CACHE_FOLDER = (Environment
            .getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ? Environment
            .getExternalStorageDirectory().getPath()
            + "/Android/data/pt.ipleiria.estg.meicm.iaupss.estgparking/cache/"
            : Environment.getDataDirectory().getPath()
            + "/data/pt.ipleiria.estg.meicm.iaupss.estgparking/cache/");*/

    private LruCache<String, Bitmap> imgCache;

    public ImageCache() {
        this.initializeCache();
    }

    public void initializeCache() {

        this.imgCache = new LruCache<String, Bitmap>(MAX_SIZE) {

            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount();
            }

            @Override
            protected void entryRemoved(boolean evicted, String key,
                                        Bitmap oldBitmap, Bitmap newBitmap) {
                super.entryRemoved(evicted, key, oldBitmap, newBitmap);
            }

        };

    }

    public synchronized void addElement(String key, Bitmap bitmap) {

        if (key != null && bitmap != null)
            if (this.imgCache.get(key) == null) {
                this.imgCache.put(key, bitmap);
                Log.i(TAG, "Add Element: " + key + "(key)");
            }
    }

    public synchronized Bitmap getElement(String key) {
		/*
		 * value = this.newCache.get(key); this.autoRemoveContext = false;
		 * this.newCache.remove(key); this.newCache.put(key, value);
		 */

        return this.imgCache.get(key);
    }

    public synchronized boolean containsElement(String key) {

        return (this.imgCache.get(key) != null);
    }

    /*public void removeImage(String imageName) {

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
    }*/
}
