package pt.ipleiria.estg.meicm.iaupss.estgparking.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.R;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Section;

public class SectionsAdapter extends RecyclerView.Adapter<SectionViewHolder> {

    private List<Section> sections;

    // Provide a suitable constructor (depends on the kind of dataset)
    public SectionsAdapter(List<Section> sections) {

        this.sections = sections;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.section_element, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //...

        return new SectionViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(SectionViewHolder holder, int position) {

        holder.bindSection(sections.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return (sections != null ? sections.size() : 0);
    }
}
