package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;

import java.util.LinkedList;
import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.adapter.SpacerItemDecoration;
import pt.ipleiria.estg.meicm.iaupss.estgparking.adapter.RankingsAdapter;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Ranking;

public class RankingsActivity extends BaseRecyclerViewActivity {

    private static final String TAG = "RANKINGS_ACTIVITY";

    private MenuItem refreshMenuItem;

    private List<Ranking> rankings;
    private List<Ranking> filterRankings;
    private boolean isFullRanking;
    private boolean isConnected;
    private boolean isWiFi;
    private boolean isUpdated;

    public RankingsActivity() {
        super(R.layout.activity_rankings, R.id.rankings_progress_frame_layout, R.id.rankings_recycler_view, R.menu.menu_rankings);
        Log.d(TAG, "Activity is Initialized.");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {

            this.isFullRanking = true;
            this.isUpdated = false;

            ConnectivityManager connectivityManager =
                    (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            isConnected = activeNetwork != null && activeNetwork.isConnected();

            isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rankings, menu);

        refreshMenuItem = menu.getItem(0);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                finish();
                return true;
            case R.id.action_refresh_ranking:

                if (super.getProgressFrameLayout().getVisibility() == FrameLayout.GONE) {
                    if (isFullRanking) {
                        if (super.getApp().getDatastore().getSyncStatus().hasIncoming) {
                            try {
                                super.getApp().getDatastore().sync();
                                isUpdated = false;
                                super.getProgressFrameLayout().setVisibility(FrameLayout.VISIBLE);
                            } catch (DbxException e) {
                                Log.e(TAG, e.getLocalizedMessage());
                            }
                        } else
                            Toast.makeText(getApplicationContext(), "O ranking está atualizado", Toast.LENGTH_SHORT).show();
                    }
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

        if (!isWiFi && datastore.getSyncStatus().hasIncoming) {
            try {
                datastore.sync();
                isUpdated = false;
            } catch (DbxException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }

        if (!datastore.getSyncStatus().isDownloading && isFullRanking && !isUpdated) {
            this.filterRankings = super.getApp().getRankingRepository().fetchRankings();

            if (this.filterRankings == null || this.filterRankings.isEmpty()) {
                Toast.makeText(getBaseContext(), "Não existe ranking de utilizadores", Toast.LENGTH_SHORT).show();
                finish();
            }

            // specify adapter
            super.setViewAdapter(new RankingsAdapter(this.filterRankings, super.getApp().getImageCache()));
            super.getRecyclerView().setAdapter(super.getViewAdapter());

            RecyclerView.ItemDecoration itemDecoration = new SpacerItemDecoration(this);
            super.getRecyclerView().addItemDecoration(itemDecoration);
            super.getRecyclerView().setItemAnimator(new DefaultItemAnimator());

            super.getProgressFrameLayout().setVisibility(FrameLayout.GONE);

            this.rankings = this.filterRankings;
            this.isUpdated = true;
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
