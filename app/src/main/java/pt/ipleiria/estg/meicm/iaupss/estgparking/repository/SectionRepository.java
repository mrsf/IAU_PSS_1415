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

    public SectionRepository(Context context, DbxDatastore datastore, String lotId) {

        this.datastore = datastore;
        this.lotId = lotId;
        //this.sectionsData = new SectionsData(context, true);
    }

    @Override
    public List<Section> fetchSections() {

        return dataStoreSections();
    }

    @Override
    public boolean occupySection(double lat, double lng) {
        return false;
    }

    @Override
    public boolean abandonSection(double lat, double lng) {
        return false;
    }

    private List<Section> dataStoreSections() {

        List<SectionsTable.SectionRecord> sectionRecords = new ArrayList<>();

        try {
            datastore.sync();
            SectionsTable sectionsTable = new SectionsTable(datastore);
            sectionRecords.addAll(sectionsTable.getSectionsSorted(this.lotId));
        } catch (DbxException e) {
            e.printStackTrace();
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

        /*this.sectionsData.open();
        this.sectionsData.insertSections(sections);
        this.sectionsData.close();*/

        return sections;
    }

    private List<Section> dataBaseSections() {

        this.sectionsData.open();
        List<Section> sections = this.sectionsData.getSections(this.lotId);
        this.sectionsData.close();

        return sections;
    }
}
