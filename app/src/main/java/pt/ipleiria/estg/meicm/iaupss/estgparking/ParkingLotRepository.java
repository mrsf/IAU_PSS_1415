package pt.ipleiria.estg.meicm.iaupss.estgparking;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.model.ParkingLot;

/**
 * Created by francisco on 05-12-2014.
 */
public class ParkingLotRepository implements IParkingLotRepository {

    private DbxDatastore datastore;

    public ParkingLotRepository(DbxDatastore datastore) {

        this.datastore = datastore;
    }


    @Override
    public List<ParkingLot> fetchParkingLots() {

        LinkedList<DbxRecord> records = new LinkedList<DbxRecord>();
        try {
            datastore.sync();
            records.addAll(datastore.getTable("parking_lot").query().asList());
        } catch (DbxException e) {
            e.printStackTrace();
        }

        LinkedList<ParkingLot> parkingLotList = new LinkedList<ParkingLot>();

        for(DbxRecord record: records) {
            ParkingLot pLot = new ParkingLot();
            pLot.setName(record.getString("nome"));
            pLot.setDescription(record.getString("descricao"));
            pLot.setLatitude(record.getDouble("latitude"));
            pLot.setLongitude(record.getDouble("longitude"));
            pLot.setImagePath(record.getString("image_path"));

            parkingLotList.add(pLot);
            Log.i("TESTE", pLot.getName() + " | " + pLot.getDescription() + " | " + pLot.getLatitude() + " | " + pLot.getLongitude());
        }

        return parkingLotList;
    }
}
