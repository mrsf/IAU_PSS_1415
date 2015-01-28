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
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Lot;

public class LotsAdapter extends RecyclerView.Adapter<LotViewHolder> {

    private final ImageCache cache;

    private ThreadPoolExecutor exec;
    private List<Lot> lots;

    // Provide a suitable constructor (depends on the kind of dataset)
    public LotsAdapter(List<Lot> lots, ImageCache cache) {

        this.exec = (ThreadPoolExecutor) AsyncTask.THREAD_POOL_EXECUTOR;
        this.exec.setCorePoolSize(1);
        this.exec.setMaximumPoolSize(6);

        this.lots = lots;
        this.cache = cache;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public LotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lot_element, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //...

        return new LotViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(LotViewHolder holder, int position) {

        holder.bindLot(lots.get(position), cache, exec);
    }

    @Override
    public void onViewRecycled(LotViewHolder holder) {
        super.onViewRecycled(holder);

        if (holder.getDownloader().getStatus() != AsyncTask.Status.FINISHED)
            holder.getDownloader().cancel(true);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return (lots != null ? lots.size() : 0);
    }
}
