package pt.ipleiria.estg.meicm.iaupss.estgparking.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.ThreadPoolExecutor;

import pt.ipleiria.estg.meicm.iaupss.estgparking.ESTGParkingApplication;
import pt.ipleiria.estg.meicm.iaupss.estgparking.ImageCache;
import pt.ipleiria.estg.meicm.iaupss.estgparking.LotDetailsActivity;
import pt.ipleiria.estg.meicm.iaupss.estgparking.R;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.DownloadTask;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Lot;
import pt.ipleiria.estg.meicm.iaupss.estgparking.task.ImageDownloader;

public class LotViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private View view;

    private Lot lot;
    private TextView nameText;
    private TextView descriptionText;
    private ImageView lotImage;
    private ProgressBar imageProgress;
    private ImageDownloader downloader;

    public LotViewHolder(View v) {

        super(v);

        v.setOnClickListener(this);

        this.view = v;

        this.lot = new Lot();
        this.nameText = (TextView) v.findViewById(R.id.textViewTeste);
        this.descriptionText = (TextView) v.findViewById(R.id.description_text_view);
        this.lotImage = (ImageView) v.findViewById(R.id.lot_image_view);
        this.imageProgress = (ProgressBar) v.findViewById(R.id.image_progress_bar);
        this.downloader = null;
    }

    protected void bindLot(Lot lot, ImageCache cache, ThreadPoolExecutor exec) {

        this.lot = lot;

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

        Animation anim = new Animation() {
            @Override
            public void start() {
                super.start();
            }
        };

        anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                view.setBackgroundColor(Color.GRAY);
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                view.setBackgroundColor(Color.TRANSPARENT);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        anim.setDuration(50);

        view.startAnimation(anim);

        Intent intent = new Intent(v.getContext(), LotDetailsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Lot", lot);

        v.getContext().startActivity(intent);

        ESTGParkingApplication app = ESTGParkingApplication.getInstance();
        if (!app.getDatastoreManager().isShutDown())
            app.getDatastoreManager().shutDown();
    }
}
