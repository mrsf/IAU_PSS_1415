package pt.ipleiria.estg.meicm.iaupss.estgparking.adapter;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import pt.ipleiria.estg.meicm.iaupss.estgparking.ImageCache;
import pt.ipleiria.estg.meicm.iaupss.estgparking.R;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Ranking;

public class RankingsAdapter extends RecyclerView.Adapter<RankingViewHolder> {

    private ThreadPoolExecutor exec;

    private List<Ranking> rankings;
    private ImageCache cache;

    // Provide a suitable constructor (depends on the kind of dataset)
    public RankingsAdapter(List<Ranking> rankings, ImageCache cache) {

        this.exec = (ThreadPoolExecutor) AsyncTask.THREAD_POOL_EXECUTOR;
        this.exec.setCorePoolSize(1);
        this.exec.setMaximumPoolSize(6);

        this.rankings = rankings;
        this.cache = cache;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RankingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ranking_element, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //...

        return new RankingViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RankingViewHolder holder, int position) {

        holder.bindRanking(rankings.get(position), cache, exec);
    }

    @Override
    public void onViewRecycled(RankingViewHolder holder) {
        super.onViewRecycled(holder);

        try {
            if (holder.getDownloader().getStatus() != AsyncTask.Status.FINISHED)
                holder.getDownloader().cancel(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return (rankings != null ? rankings.size() : 0);
    }
}
