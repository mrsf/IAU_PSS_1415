package pt.ipleiria.estg.meicm.iaupss.estgparking.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import pt.ipleiria.estg.meicm.iaupss.estgparking.MapActivity;
import pt.ipleiria.estg.meicm.iaupss.estgparking.R;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Section;

public class SectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private View view;

    private Section section;
    private FrameLayout statusColorFrame;
    private TextView nameText;
    private TextView descriptionText;
    private TextView occupationText;

    public SectionViewHolder(View v) {
        super(v);

        v.setOnClickListener(this);

        this.view = v;

        this.section = new Section();
        this.statusColorFrame = (FrameLayout) v.findViewById(R.id.element_section_status_color_frame_layout);
        this.nameText = (TextView) v.findViewById(R.id.element_section_name_text_view);
        this.descriptionText = (TextView) v.findViewById(R.id.element_section_description_text_view);
        this.occupationText = (TextView) v.findViewById(R.id.element_section_occupation_text_view);
    }

    protected void bindSection(Section section) {

        this.section = section;

        this.statusColorFrame.setBackgroundColor(section.getStatusColor());
        this.nameText.setText(section.getName());
        this.descriptionText.setText(section.getDescription());
        this.occupationText.setText(String.valueOf(section.getCapacity() - section.getOccupation()) + ((section.getCapacity() - section.getOccupation()) > 1 ? " Vagas" : " Vaga"));
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
                view.setBackgroundColor(Color.parseColor("#23000000"));
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

        /*Intent intent = new Intent(v.getContext(), LotDetailsActivity.class);
        intent.putExtra("ParkingLot", parkingLot);

        v.getContext().startActivity(intent);*/

        Intent intent = new Intent(v.getContext(), MapActivity.class);
        intent.putExtra("Section", section);
        v.getContext().startActivity(intent);

        //Toast.makeText(v.getContext(), section.getName(), Toast.LENGTH_SHORT).show();
    }
}
