package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastoreManager;
import com.dropbox.sync.android.DbxException;

@SuppressLint("ValidFragment")
public class DropboxFragment extends Fragment {

    private static final String TAG = "DROPBOX_FRAGMENT";

    private int REQUEST_LINK_TO_DBX = 0;

    private DbxAccountManager mAccountManager;
    private DbxDatastoreManager mDatastoreManager;

    public DropboxFragment(DbxAccountManager accountManager, DbxDatastoreManager datastoreManager) {

        mAccountManager = accountManager;
        mDatastoreManager = datastoreManager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_dropbox, container, false);

        (v.findViewById(R.id.dropbox_image_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAccountManager.startLink(getActivity(), REQUEST_LINK_TO_DBX);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LINK_TO_DBX) {
            if (resultCode == Activity.RESULT_OK) {
                DbxAccount account = mAccountManager.getLinkedAccount();
                try {
                    // Migrate any local datastores to the linked account
                    mDatastoreManager.migrateToAccount(account);
                    // Now use Dropbox datastores
                    mDatastoreManager = DbxDatastoreManager.forAccount(account);
                } catch (DbxException e) {
                    e.printStackTrace();
                }
            } else {
                // Link failed or was cancelled by the user
                Log.d(TAG, "Link failed or was cancelled by the user");
                Toast.makeText(this.getActivity(), "A autenticação falhou", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
