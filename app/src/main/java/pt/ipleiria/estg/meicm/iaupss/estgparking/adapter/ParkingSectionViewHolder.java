package pt.ipleiria.estg.meicm.iaupss.estgparking.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import pt.ipleiria.estg.meicm.iaupss.estgparking.R;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.ParkingSection;

/**
 * Created by francisco on 18-12-2014.
 */
public class ParkingSectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ParkingSection parkingSection;
    private TextView nameText;
    private TextView descriptionText;

    public ParkingSectionViewHolder(View v) {
        super(v);

        v.setOnClickListener(this);

        this.parkingSection = new ParkingSection();
        this.nameText = (TextView) v.findViewById(R.id.element_section_name_text_view);
        this.descriptionText = (TextView) v.findViewById(R.id.element_section_description_text_view);
    }

    protected void bindSection(ParkingSection section) {

        this.parkingSection = section;

        this.nameText.setText(section.getName());
        this.descriptionText.setText(section.getDescription());
    }

    @Override
    public void onClick(View v) {

        /*Intent intent = new Intent(v.getContext(), LotDetailsActivity.class);
        intent.putExtra("ParkingLot", parkingLot);

        v.getContext().startActivity(intent);*/
        Toast.makeText(v.getContext(), this.nameText.getText(), Toast.LENGTH_SHORT).show();
    }
}
