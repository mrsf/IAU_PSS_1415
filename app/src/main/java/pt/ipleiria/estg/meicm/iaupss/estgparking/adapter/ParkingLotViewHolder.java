package pt.ipleiria.estg.meicm.iaupss.estgparking.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ThreadPoolExecutor;

import pt.ipleiria.estg.meicm.iaupss.estgparking.ImageCache;
import pt.ipleiria.estg.meicm.iaupss.estgparking.R;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.DownloadTask;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.ParkingLot;
import pt.ipleiria.estg.meicm.iaupss.estgparking.task.ImageDownloader;

/**
 * Created by francisco on 07-12-2014.
 */
public class ParkingLotViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView nameText;
    private TextView descriptionText;
    private ImageView lotImage;
    private ProgressBar imageProgress;
    private ImageDownloader downloader;

    public ParkingLotViewHolder(View v) {

        super(v);

        v.setOnClickListener(this);

        this.nameText = (TextView) v.findViewById(R.id.textViewTeste);
        this.descriptionText = (TextView) v.findViewById(R.id.description_text_view);
        this.lotImage = (ImageView) v.findViewById(R.id.lot_image_view);
        this.imageProgress = (ProgressBar) v.findViewById(R.id.image_progress_bar);
        this.downloader = null;
    }

    protected void bindLot(ParkingLot lot, ImageCache cache, ThreadPoolExecutor exec) {
        this.nameText.setText(lot.getName());
        this.descriptionText.setText(lot.getDescription());
        this.downloader = new ImageDownloader(cache);
        this.downloader.executeOnExecutor(exec, new DownloadTask(lot.getImagePath(), this.lotImage, this.imageProgress));
    }

    protected ImageDownloader getDownloader() {
        return downloader;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(v.getContext(), this.nameText.getText(), Toast.LENGTH_LONG).show();
    }
}
