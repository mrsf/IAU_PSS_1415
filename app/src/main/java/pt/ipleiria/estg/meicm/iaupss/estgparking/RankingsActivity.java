package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.dropbox.sync.android.DbxDatastore;

import java.util.LinkedList;
import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.adapter.RankingsAdapter;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Ranking;

public class RankingsActivity extends BaseRecyclerViewActivity {

    private static final String TAG = "RANKINGS_ACTIVITY";

    private MenuItem refreshMenuItem;
    private List<Ranking> rankings;
    private List<Ranking> filterRankings;
    private boolean isFullRanking;

    public RankingsActivity() {
        super(R.layout.activity_rankings, R.id.rankings_progress_frame_layout, R.id.rankings_recycler_view, R.menu.menu_rankings);
        Log.d(TAG, "Activity is Initialized.");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null)
            this.isFullRanking = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (super.onCreateOptionsMenu(menu))
            this.refreshMenuItem = menu.getItem(0);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh_ranking:

                if (super.getProgressFrameLayout().getVisibility() == FrameLayout.GONE) {
                        if (isFullRanking && super.getApp().getDatastore().getSyncStatus().hasIncoming) {
                                super.updateDatastore();
                        } else
                            Toast.makeText(getApplicationContext(), "O ranking está atualizado", Toast.LENGTH_SHORT).show();
                }

                return true;
            case R.id.action_my_ranking:

                if (super.getProgressFrameLayout().getVisibility() == FrameLayout.GONE) {
                    if (isFullRanking) {
                        refreshMenuItem.setVisible(false);
                        item.setIcon(R.drawable.ic_action_group);
                        item.setTitle(R.string.action_all_rankings);
                        this.isFullRanking = false;
                        this.filterRankings = this.getMyRanking(super.getApp().getUserInfoProvider().getEmail());
                    } else {
                        refreshMenuItem.setVisible(true);
                        item.setIcon(R.drawable.ic_action_person);
                        item.setTitle(R.string.action_my_ranking);
                        this.isFullRanking = true;
                        this.filterRankings = this.rankings;
                    }

                    super.setViewAdapter(new RankingsAdapter(filterRankings, super.getApp().getImageCache()));
                    super.getRecyclerView().setAdapter(super.getViewAdapter());
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDatastoreStatusChange(DbxDatastore datastore) {

        Log.d(TAG, datastore.getSyncStatus().toString());

        if (!datastore.getSyncStatus().needsReset && datastore.getSyncStatus().isConnected && super.isConnected()) {

            super.updateIfWifi(datastore);

            if (!datastore.getSyncStatus().isDownloading && !super.isDataLoaded() && this.isFullRanking) {

                if (!super.isUpdated())
                    this.filterRankings = super.getApp().getRankingRepository(true).fetchRankings();
                else
                    this.filterRankings = super.getApp().getRankingRepository(false).fetchRankings();

                if (this.filterRankings == null || this.filterRankings.isEmpty()) {
                    Toast.makeText(getBaseContext(), "Não existe ranking de utilizadores", Toast.LENGTH_SHORT).show();
                    super.onBackPressed();
                }

                // specify adapter
                super.specifyAdapter(new RankingsAdapter(this.filterRankings, super.getApp().getImageCache()));

                this.rankings = this.filterRankings;

            }

        } else {
            Toast.makeText(getApplicationContext(), "Problema na conexão com o dropbox.", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
    }

    private List<Ranking> getMyRanking(String email) {

        List<Ranking> myRanking = new LinkedList<>();

        for (Ranking ranking: rankings) {
            if (ranking.getEmail().equals(email)) {
                myRanking.add(ranking);
                break;
            }
        }

        return myRanking;
    }
}
