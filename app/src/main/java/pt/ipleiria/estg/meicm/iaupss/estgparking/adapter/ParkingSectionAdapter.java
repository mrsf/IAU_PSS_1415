package pt.ipleiria.estg.meicm.iaupss.estgparking.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.R;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.ParkingSection;

/**
 * Created by francisco on 18-12-2014.
 */
public class ParkingSectionAdapter extends RecyclerView.Adapter<ParkingSectionViewHolder> {

    private List<ParkingSection> sections;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ParkingSectionAdapter(List<ParkingSection> sections) {

        this.sections = sections;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ParkingSectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.parking_section_element, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //...

        ParkingSectionViewHolder vh = new ParkingSectionViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ParkingSectionViewHolder holder, int position) {

        holder.bindSection(sections.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return (sections != null ? sections.size() : 0);
    }
}
