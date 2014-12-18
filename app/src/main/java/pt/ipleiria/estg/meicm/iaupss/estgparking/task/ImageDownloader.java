package pt.ipleiria.estg.meicm.iaupss.estgparking.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import pt.ipleiria.estg.meicm.iaupss.estgparking.ImageCache;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.DownloadTask;

/**
 * Created by francisco on 11-12-2014.
 */
public class ImageDownloader extends AsyncTask<DownloadTask, DownloadTask, DownloadTask> {

    private static final String TAG = "IMAGE_DOWNLOADER";
    private static final String CACHE_FOLDER = (Environment
            .getExternalStorageState() == Environment.MEDIA_MOUNTED ? Environment
            .getExternalStorageDirectory().getPath().toString()
            + "/Android/data/pt.ipleiria.estg.meicm.iaupss.estgparking/cache/"
            : Environment.getDataDirectory().getPath().toString()
            + "/data/pt.ipleiria.estg.meicm.iaupss.estgparking/cache/");

    private final ImageCache imageCache;

    private DownloadTask task;
    private boolean finished;

    public ImageDownloader(ImageCache imageCache) {
        this.imageCache = imageCache;
        this.finished = false;
    }

    @Override
    protected DownloadTask doInBackground(DownloadTask... tasks) {

        File dir = this.createDirIfNotExits();

        this.task = tasks[0];
        String imageUrl = task.getUrl();
        String checksum = this.imageCache.generateChecksum(imageUrl);

        if (!imageCache.containsElement(imageUrl)) {
            File imgFile = new File(CACHE_FOLDER + checksum);
            if (!imgFile.exists()) {
                Bitmap imageBitmap = this.imageDownload(imageUrl);
                task.setImage(imageBitmap);
                this.publishProgress(task);

                this.createImage(dir, imageBitmap, checksum);
            } else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;
                options.inSampleSize = 1;
                task.setImage(BitmapFactory.decodeFile(CACHE_FOLDER
                        + checksum, options));
                this.publishProgress(task);
            }
        } else {
            return task;
        }

        this.finished = true;
        return null;
    }

    @Override
    protected void onProgressUpdate(DownloadTask... values) {
        super.onProgressUpdate(values);

        for (DownloadTask t : values) {
            t.getImageView().setImageBitmap(t.getImage());

            this.imageCache.addElement(t.getUrl(), t.getImage());

            t.getProgressBar().setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPostExecute(DownloadTask downloadTask) {
        super.onPostExecute(downloadTask);

        if (downloadTask != null) {
            downloadTask.getImageView().setImageBitmap(imageCache.getElement(downloadTask.getUrl()));
            downloadTask.getProgressBar().setVisibility(View.GONE);
        }
    }

    private InputStream OpenHttpConnection(String urlString) throws IOException {
        InputStream in = null;
        int response = -1;

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection)) {
            throw new IOException("Not an HTTP connection");
        }
        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        } catch (Exception ex) {
            Log.d(TAG, ex.getLocalizedMessage());
            throw new IOException("Error connecting");
        }
        return in;
    }

    private Bitmap imageDownload(String imageUrl) {
        Bitmap bitmap = null;
        InputStream inputStream = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[1024/2];
        options.inSampleSize = 2;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        try {
            inputStream = OpenHttpConnection(imageUrl);
            bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();
        } catch (IOException e1) {
            Log.d(TAG, e1.getLocalizedMessage());
        }
        return bitmap;
    }

    private File createDirIfNotExits() {
        String filePath = CACHE_FOLDER;

        File cacheDir = new File(filePath);
        if (!cacheDir.exists())
            cacheDir.mkdirs();

        return cacheDir;
    }

    private void createImage(File dir, Bitmap result, String fileName) {

        try {
            File file = new File(dir, fileName);
            FileOutputStream fOut = new FileOutputStream(file);

            result.compress(Bitmap.CompressFormat.PNG, 75, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException ioe) {
            Log.d(TAG, ioe.getLocalizedMessage());
        }
    }

    public ImageCache getImageCache() {
        return imageCache;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isFinished() {
        return finished;
    }
}
