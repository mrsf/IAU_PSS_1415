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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;

import pt.ipleiria.estg.meicm.iaupss.estgparking.HashConvertion;
import pt.ipleiria.estg.meicm.iaupss.estgparking.ImageCache;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.DownloadTask;

public class ImageDownloader extends AsyncTask<DownloadTask, DownloadTask, DownloadTask> {

    private static final String TAG = "IMAGE_DOWNLOADER";
    private static final String CACHE_FOLDER = (Environment
            .getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ? Environment
            .getExternalStorageDirectory().getPath()
            + "/Android/data/pt.ipleiria.estg.meicm.iaupss.estgparking/cache/"
            : Environment.getDataDirectory().getPath()
            + "/data/pt.ipleiria.estg.meicm.iaupss.estgparking/cache/");

    private final ImageCache imageCache;

    public ImageDownloader(ImageCache imageCache) {

        this.imageCache = imageCache;
    }

    @Override
    protected DownloadTask doInBackground(DownloadTask... tasks) {

        File dir = this.createDirIfNotExits();

        DownloadTask task = tasks[0];
        String imageUrl = task.getUrl();
        //String checksum = this.imageCache.generateChecksum(imageUrl);
        String checksum = null;
        try {
            checksum = HashConvertion.SHA1(imageUrl);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

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
        int response;

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
        InputStream inputStream;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[1024 * 16];
        options.inSampleSize = 1;
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
}
