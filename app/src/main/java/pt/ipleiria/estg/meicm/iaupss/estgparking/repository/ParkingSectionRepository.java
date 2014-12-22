package pt.ipleiria.estg.meicm.iaupss.estgparking.repository;

import android.content.Context;
import android.util.Log;

import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.database.SectionsRepository;
import pt.ipleiria.estg.meicm.iaupss.estgparking.datastore.ParkingSectionsTable;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.ParkingSection;

public class ParkingSectionRepository implements IParkingSectionRepository {

    private static final String TAG = "FETCH_PARKING_SECTION";

    private DbxDatastore datastore;
    private String lotId;
    private SectionsRepository sectionsRepository;

    public ParkingSectionRepository(Context context, DbxDatastore datastore, String lotId) {

        this.datastore = datastore;
        this.lotId = lotId;
        this.sectionsRepository = new SectionsRepository(context, false);
    }

    @Override
    public List<ParkingSection> fetchParkingSections() {

        List<ParkingSectionsTable.ParkingSectionRecord> records = new ArrayList<>();
        try {
            datastore.sync();
            ParkingSectionsTable sectionsTable = new ParkingSectionsTable(datastore);
            records.addAll(sectionsTable.getParkingSectionsSorted(lotId));
        } catch (DbxException e) {
            e.printStackTrace();
        }

        List<ParkingSection> parkingSectionList = new LinkedList<>();

        for(ParkingSectionsTable.ParkingSectionRecord record: records) {
            ParkingSection pSection = new ParkingSection();
            pSection.setId(record.getId());
            pSection.setName(record.getName());
            pSection.setDescription(record.getDescription());
            pSection.setLatitude(record.getLatitude());
            pSection.setLongitude(record.getLongitude());
            pSection.setCapacity(record.getCapacity());
            pSection.setOccupation(record.getOccupation());
            pSection.setLotId(record.getLotId());

            parkingSectionList.add(pSection);
            Log.i(TAG, "Add: " + pSection.getId() + " | " + pSection.getName() + " | " + pSection.getDescription()
                       + " | " + pSection.getLatitude() + " | " + pSection.getLongitude() + " | " + pSection.getCapacity()
                       + " | " + pSection.getOccupation() + " | " + pSection.getLotId());
        }

        return parkingSectionList;
    }

    private List<ParkingSection> dataStoreParkingSections() {

        List<ParkingSectionsTable.ParkingSectionRecord> records = new ArrayList<>();

        try {
            datastore.sync();
            ParkingSectionsTable sectionsTable = new ParkingSectionsTable(datastore);
            records.addAll(sectionsTable.getParkingSectionsSorted(this.lotId));
        } catch (DbxException e) {
            e.printStackTrace();
        }

        List<ParkingSection> parkingSections = new LinkedList<>();

        for(ParkingSectionsTable.ParkingSectionRecord record: records) {

            ParkingSection pSection = new ParkingSection();

            pSection.setId(record.getId());
            pSection.setName(record.getName());
            pSection.setDescription(record.getDescription());
            pSection.setLatitude(record.getLatitude());
            pSection.setLongitude(record.getLongitude());
            pSection.setCapacity(record.getCapacity());
            pSection.setOccupation(record.getOccupation());
            pSection.setLotId(record.getLotId());

            parkingSections.add(pSection);

            Log.d(TAG, "Add: " + pSection.getId() + " | " + pSection.getName() + " | " + pSection.getDescription()
                    + " | " + pSection.getLatitude() + " | " + pSection.getLongitude() + " | " + pSection.getCapacity()
                    + " | " + pSection.getOccupation() + " | " + pSection.getLotId());
        }

        this.sectionsRepository.open();
        this.sectionsRepository.insertSections(parkingSections);
        this.sectionsRepository.close();

        return parkingSections;
    }

    private List<ParkingSection> dataBaseParkingSections() {

        this.sectionsRepository.open();
        List<ParkingSection> parkingSections = this.sectionsRepository.getSections(this.lotId);
        this.sectionsRepository.close();

        return parkingSections;
    }
}
