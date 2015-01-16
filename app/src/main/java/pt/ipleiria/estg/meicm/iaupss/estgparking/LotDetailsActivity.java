package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import pt.ipleiria.estg.meicm.iaupss.estgparking.model.DownloadTask;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Lot;
import pt.ipleiria.estg.meicm.iaupss.estgparking.task.ImageDownloader;

public class LotDetailsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lot_details);

        if (savedInstanceState == null) {

            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment(getIntent()))
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        //System.exit(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lot_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    @SuppressLint("ValidFragment")
    public static class PlaceholderFragment extends Fragment {

        private ImageDownloader downloader;
        private ImageCache cache;

        private Intent intent;
        private Lot lot;

        private ImageView lotImage;
        private ProgressBar imageProgress;
        private TextView nameText;
        private TextView descriptionText;
        private TextView coordinatesText;
        private Button showSections;

        public PlaceholderFragment() {}

        public PlaceholderFragment(Intent intent) {
            this.intent = intent;
            this.cache = new ImageCache();
            this.downloader = new ImageDownloader(cache);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_lot_details, container, false);

            if (this.lot == null)
                this.lot = intent.getParcelableExtra("Lot");

            this.lotImage = (ImageView) rootView.findViewById(R.id.detail_lot_image_view);
            this.imageProgress = (ProgressBar) rootView.findViewById(R.id.detail_image_progress_bar);
            this.nameText = (TextView) rootView.findViewById(R.id.detail_name_text_view);
            this.descriptionText = (TextView) rootView.findViewById(R.id.detail_description_text_view);
            this.coordinatesText = (TextView) rootView.findViewById(R.id.detail_coordinates_text_view);
            this.showSections = (Button) rootView.findViewById(R.id.show_sections_button);
            this.showSections.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), SectionsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("LotId", lot.getId());

                    v.getContext().startActivity(intent);
                }
            });

            this.downloader.execute(new DownloadTask(this.lot.getImagePath(), this.lotImage, this.imageProgress));

            this.nameText.setText(this.nameText.getText() + this.lot.getName());
            this.descriptionText.setText(this.descriptionText.getText() + this.lot.getDescription());
            this.coordinatesText.setText(this.coordinatesText.getText() +
                                            String.valueOf(this.lot.getLatitudeA())
                                            + " ; " + String.valueOf(this.lot.getLongitudeA()));

            return rootView;
        }
    }
}
