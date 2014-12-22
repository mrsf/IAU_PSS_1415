package pt.ipleiria.estg.meicm.iaupss.estgparking.model;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class DownloadTask {

    private String url;
    private ImageView imageView;
    private Bitmap imageToLoad;
    private ProgressBar progressBar;

    public DownloadTask(String url, ImageView imageView, ProgressBar progressBar) {
        this.url = url;
        this.imageView = imageView;
        this.imageToLoad = null;
        this.progressBar = progressBar;
    }

    public Bitmap getImage() {
        return imageToLoad;
    }

    public void setImage(Bitmap imageToLoad) {
        this.imageToLoad = imageToLoad;
    }

    public String getUrl() {
        return url;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}
