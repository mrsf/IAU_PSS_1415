package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.content.ClipData;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;

import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.adapter.SpacerItemDecoration;
import pt.ipleiria.estg.meicm.iaupss.estgparking.adapter.RankingsAdapter;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Ranking;

public class RankingsActivity extends BaseRecyclerViewActivity {

    private static final String TAG = "RANKINGS_ACTIVITY";

    private boolean showFullRanking;

    public RankingsActivity() {
        super(R.layout.activity_rankings, R.id.rankings_progress_bar, R.id.rankings_recycler_view, R.menu.menu_rankings);
        Log.d(TAG, "Activity is Initialized.");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.showFullRanking = true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                finish();
                return true;
            case R.id.action_my_ranking:

                List<Ranking> rankings;

                if (showFullRanking) {
                    item.setIcon(R.drawable.ic_action_group);
                    this.showFullRanking = false;
                    rankings = super.getApp().getRankingRepository().fetchMyRanking(super.getApp().getUserInfoProvider().getEmail());
                } else {
                    item.setIcon(R.drawable.ic_action_person);
                    this.showFullRanking = true;
                    rankings = super.getApp().getRankingRepository().fetchRankings();
                }

                super.setViewAdapter(new RankingsAdapter(rankings, super.getApp().getImageCache()));
                super.getRecyclerView().setAdapter(super.getViewAdapter());

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDatastoreStatusChange(DbxDatastore datastore) {

        List<Ranking> rankings;

        if (datastore.getSyncStatus().hasIncoming) {
            try {
                datastore.sync();
            } catch (DbxException e) {
                e.printStackTrace();
            }
        } /*else {
            rankings = super.getApp().getRankingRepository().fetchRankings();
        }*/

        if (showFullRanking) {
            rankings = super.getApp().getRankingRepository().fetchRankings();

            if (rankings == null || rankings.isEmpty()) {
                Toast.makeText(getBaseContext(), "Não existe ranking de utilizadores", Toast.LENGTH_SHORT).show();
                finish();
            }

            // specify adapter
            super.setViewAdapter(new RankingsAdapter(rankings, super.getApp().getImageCache()));
            super.getRecyclerView().setAdapter(super.getViewAdapter());

            RecyclerView.ItemDecoration itemDecoration = new SpacerItemDecoration(this);
            super.getRecyclerView().addItemDecoration(itemDecoration);
            super.getRecyclerView().setItemAnimator(new DefaultItemAnimator());

            super.getProgressBar().setVisibility(ProgressBar.GONE);
        }
    }
}
