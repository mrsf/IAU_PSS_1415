package pt.ipleiria.estg.meicm.iaupss.estgparking.repository;

import android.util.Log;

import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.datastore.ParkingSectionsTable;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.ParkingSection;

/**
 * Created by francisco on 19-12-2014.
 */
public class ParkingSectionRepository implements IParkingSectionRepository {

    private DbxDatastore datastore;
    private String lotId;

    public ParkingSectionRepository(DbxDatastore datastore, String lotId) {

        this.datastore = datastore;
        this.lotId = lotId;
    }

    @Override
    public List<ParkingSection> fetchParkingSections() {

        List<ParkingSectionsTable.ParkingSectionRecord> records = new ArrayList<ParkingSectionsTable.ParkingSectionRecord>();
        try {
            datastore.sync();
            ParkingSectionsTable sectionsTable = new ParkingSectionsTable(datastore);
            records.addAll(sectionsTable.getParkingSectionsSorted(lotId));
        } catch (DbxException e) {
            e.printStackTrace();
        }

        List<ParkingSection> parkingSectionList = new LinkedList<ParkingSection>();

        for(ParkingSectionsTable.ParkingSectionRecord record: records) {
            ParkingSection pSection = new ParkingSection();
            pSection.setName(record.getName());
            pSection.setDescription(record.getDescription());
            pSection.setLatitude(record.getLatitude());
            pSection.setLongitude(record.getLongitude());
            pSection.setCapacity(record.getCapacity());
            pSection.setOccupation(record.getOccupation());
            pSection.setLotId(record.getLotId());

            parkingSectionList.add(pSection);
            Log.i("TESTE", pSection.getName() + " | " + pSection.getDescription() + " | " + pSection.getLatitude() + " | " + pSection.getLongitude());
        }

        return parkingSectionList;
    }
}
