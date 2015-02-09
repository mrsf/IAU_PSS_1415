package pt.ipleiria.estg.meicm.iaupss.estgparking.repository;

import android.content.Context;
import android.util.Log;

import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.database.SectionsData;
import pt.ipleiria.estg.meicm.iaupss.estgparking.datastore.SectionsTable;
import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Section;

public class SectionRepository implements ISectionRepository {

    private static final String TAG = "SECTION_REPOSITORY";

    private DbxDatastore datastore;
    private String lotId;
    private SectionsData sectionsData;
    private boolean isUpdate;

    public SectionRepository(Context context, DbxDatastore datastore, String lotId, boolean isUpdate) {

        this.datastore = datastore;
        this.lotId = lotId;
        this.isUpdate = isUpdate;
        this.sectionsData = new SectionsData(context, isUpdate);
    }

    @Override
    public List<Section> fetchSections() {

        if (isUpdate || this.dataBaseSections().isEmpty()) {
            Log.d(TAG, "DATASTORE");
            return this.dataStoreSections();
        } else {
            Log.d(TAG, "DATABASE");
            return this.dataBaseSections();
        }
    }

    @Override
    public boolean occupySection(double lat, double lng) {

        boolean isValid = false;

        try {
            this.datastore.sync();
            SectionsTable sectionsTable = new SectionsTable(datastore);
            isValid = sectionsTable.occupationIncrement(lat, lng);
        } catch (DbxException e) {
            Log.d(TAG, e.getLocalizedMessage());
        }

        return isValid;
    }

    @Override
    public boolean abandonSection(double lat, double lng) {

        boolean isValid = false;

        try {
            this.datastore.sync();
            SectionsTable sectionsTable = new SectionsTable(datastore);
            isValid = sectionsTable.occupationDecrement(lat, lng);
        } catch (DbxException e) {
            Log.d(TAG, e.getLocalizedMessage());
        }

        return isValid;
    }

    private List<Section> dataStoreSections() {

        List<SectionsTable.SectionRecord> sectionRecords = new ArrayList<>();

        try {
            datastore.sync();
            SectionsTable sectionsTable = new SectionsTable(datastore);
            sectionRecords.addAll(sectionsTable.getSectionsSorted(this.lotId));
        } catch (DbxException e) {
            Log.d(TAG, e.getLocalizedMessage());
        }

        List<Section> sections = new LinkedList<>();

        for(SectionsTable.SectionRecord record: sectionRecords) {

            Section section = new Section();

            section.setId(record.getId());
            section.setName(record.getName());
            section.setDescription(record.getDescription());
            section.setLatitudeA(record.getLatitudeA());
            section.setLongitudeA(record.getLongitudeA());
            section.setLatitudeB(record.getLatitudeB());
            section.setLongitudeB(record.getLongitudeB());
            section.setLatitudeC(record.getLatitudeC());
            section.setLongitudeC(record.getLongitudeC());
            section.setLatitudeD(record.getLatitudeD());
            section.setLongitudeD(record.getLongitudeD());
            section.setCapacity(record.getCapacity());
            section.setOccupation(record.getOccupation());
            section.setLotId(record.getLotId());

            sections.add(section);

            Log.d(TAG, "Add: " + section.getId() + " | " + section.getName() + " | " + section.getDescription()
                    + " | " + section.getCapacity() + " | " + section.getOccupation() + " | " + section.getLotId());
        }

        this.sectionsData.open();
        this.sectionsData.insertSections(sections);
        this.sectionsData.close();

        return sections;
    }

    private List<Section> dataBaseSections() {

        try {
            this.datastore.sync();
        } catch (DbxException e) {
            Log.d(TAG, e.getLocalizedMessage());
        }

        this.sectionsData.open();
        List<Section> sections = this.sectionsData.getSections(this.lotId);
        this.sectionsData.close();

        return sections;
    }
}
