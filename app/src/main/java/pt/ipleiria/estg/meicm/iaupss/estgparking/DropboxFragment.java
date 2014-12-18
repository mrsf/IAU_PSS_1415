package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.dropbox.sync.android.DbxAccountManager;

/**
 * Created by francisco on 02-12-2014.
 */
@SuppressLint("ValidFragment")
public class DropboxFragment extends Fragment {

    private DbxAccountManager mAccountManager;

    private int REQUEST_LINK_TO_DBX = 0;

    public DropboxFragment(DbxAccountManager accountManager) {

        mAccountManager = accountManager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_dropbox, container, false);

        ((ImageButton) v.findViewById(R.id.dropbox_image_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAccountManager.startLink(getActivity(), REQUEST_LINK_TO_DBX);
            }
        });

        return v;
    }
}
