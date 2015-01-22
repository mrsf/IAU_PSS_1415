package pt.ipleiria.estg.meicm.iaupss.estgparking.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ThreadPoolExecutor;

import pt.ipleiria.estg.meicm.iaupss.estgparking.ImageCache;
import pt.ipleiria.estg.meicm.iaupss.estgparking.R;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.DownloadTask;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Ranking;
import pt.ipleiria.estg.meicm.iaupss.estgparking.task.ImageDownloader;

public class RankingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private View view;

    private Ranking ranking;
    private TextView nameText;
    private TextView emailText;
    private ImageView rankingImage;
    private TextView scoreText;
    private RatingBar ratingBar;
    private ProgressBar imageProgress;
    private ImageDownloader downloader;

    public RankingViewHolder(View v) {

        super(v);

        v.setOnClickListener(this);

        this.view = v;

        this.ranking = new Ranking();
        this.nameText = (TextView) v.findViewById(R.id.ranking_user_name_text_view);
        this.emailText = (TextView) v.findViewById(R.id.ranking_user_email_text_view);
        this.rankingImage = (ImageView) v.findViewById(R.id.ranking_user_photo_image_view);
        this.scoreText = (TextView) v.findViewById(R.id.ranking_user_score_text_view);
        this.ratingBar = (RatingBar) v.findViewById(R.id.ranking_user_rating_bar);
        this.imageProgress = (ProgressBar) v.findViewById(R.id.ranking_user_photo_progress_bar);
        this.downloader = null;
    }

    protected void bindRanking(Ranking ranking, ImageCache cache, ThreadPoolExecutor exec) {

        this.ranking = ranking;

        this.nameText.setText(ranking.getName());
        this.emailText.setText(ranking.getEmail());
        this.scoreText.setText(ranking.getScore() + " pontos");
        this.ratingBar.setRating(ranking.getRating());
        if (!this.ranking.getImagePath().equals(null) && !this.ranking.getImagePath().equals("")) {
            this.downloader = new ImageDownloader(cache);
            this.downloader.executeOnExecutor(exec, new DownloadTask(ranking.getImagePath(), this.rankingImage, this.imageProgress));
        } else {
            this.imageProgress.setVisibility(ProgressBar.GONE);
        }
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

        Toast.makeText(v.getContext(), "Pontos: " + ranking.getScore(), Toast.LENGTH_SHORT).show();
    }
}
