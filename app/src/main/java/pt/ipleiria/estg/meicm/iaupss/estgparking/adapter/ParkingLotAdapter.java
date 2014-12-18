package pt.ipleiria.estg.meicm.iaupss.estgparking.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import pt.ipleiria.estg.meicm.iaupss.estgparking.ImageCache;
import pt.ipleiria.estg.meicm.iaupss.estgparking.R;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.ParkingLot;

/**
 * Created by francisco on 19-11-2014.
 */
public class ParkingLotAdapter extends RecyclerView.Adapter<ParkingLotViewHolder> {

    private ThreadPoolExecutor exec;

    private List<ParkingLot> lots;
    private ImageCache cache;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ParkingLotAdapter(List<ParkingLot> lots, ImageCache cache) {

        this.exec = (ThreadPoolExecutor) AsyncTask.THREAD_POOL_EXECUTOR;
        this.exec.setCorePoolSize(1);
        this.exec.setMaximumPoolSize(6);

        this.lots = lots;
        this.cache = cache;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ParkingLotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.parking_lot_element, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //...

        ParkingLotViewHolder vh = new ParkingLotViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ParkingLotViewHolder holder, int position) {

        holder.bindLot(lots.get(position), cache, exec);
    }

    @Override
    public void onViewRecycled(ParkingLotViewHolder holder) {
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
