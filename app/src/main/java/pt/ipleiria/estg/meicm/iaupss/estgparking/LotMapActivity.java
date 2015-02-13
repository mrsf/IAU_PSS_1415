package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Lot;
import pt.ipleiria.estg.meicm.iaupss.estgparking.utils.Dot;
import pt.ipleiria.estg.meicm.iaupss.estgparking.utils.Rectangle;

public class LotMapActivity extends ActionBarActivity {

    private Lot lot;

    private LatLng lotLocation;

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lot_map);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        if (this.lot == null) {
            Intent i = getIntent();
            this.lot = i.getParcelableExtra("Lot");
            if (this.lot != null) {
                Rectangle rectangle = new Rectangle();
                rectangle.setDotA(new Dot(this.lot.getLatitudeA(), this.lot.getLongitudeA()));
                rectangle.setDotB(new Dot(this.lot.getLatitudeB(), this.lot.getLongitudeB()));
                rectangle.setDotC(new Dot(this.lot.getLatitudeC(), this.lot.getLongitudeC()));
                rectangle.setDotD(new Dot(this.lot.getLatitudeD(), this.lot.getLongitudeD()));
                Dot dot = rectangle.getCenterDot();
                lotLocation = new LatLng(dot.getLat(), dot.getLng());
            }
        }

        googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.lot_map)).getMap();

        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lotLocation, 18));

        if (this.lot != null) {

            googleMap.addMarker(new MarkerOptions().position(lotLocation)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.parking_spot_marker)).title((lot != null ? lot.getName() : getResources().getString(R.string.lot_map_activity_mark_text))))
                    .setSnippet((lot != null ? lot.getDescription() : null));

            // Instantiates a new Polyline object and adds points to define a rectangle
            PolylineOptions rectOptions = new PolylineOptions()
                    .add(new LatLng(this.lot.getLatitudeA(), this.lot.getLongitudeA()))
                    .add(new LatLng(this.lot.getLatitudeB(), this.lot.getLongitudeB()))
                    .add(new LatLng(this.lot.getLatitudeC(), this.lot.getLongitudeC()))
                    .add(new LatLng(this.lot.getLatitudeD(), this.lot.getLongitudeD()))
                    .add(new LatLng(this.lot.getLatitudeA(), this.lot.getLongitudeA())); // Closes the polyline.

            rectOptions.color(Color.BLUE);

            // Get back the mutable Polyline
            googleMap.addPolyline(rectOptions);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_lot_map, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
