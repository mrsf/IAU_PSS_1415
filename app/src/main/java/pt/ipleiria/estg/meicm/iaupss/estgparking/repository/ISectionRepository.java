package pt.ipleiria.estg.meicm.iaupss.estgparking.repository;

import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Section;

public interface ISectionRepository {

    List<Section> fetchSections();

    /**
     * Increment the occupation counter of a parking section.
     *
     * @param lat - section latitude coordinate
     * @param lng - section longitude coordinate
     * @return true if latitude and longitude are inside a known section and a parking
     * spot was marked as occupied, false otherwise.
     */
    boolean occupySection(double lat, double lng);

    /**
     * Decrement the occupation counter of a parking section.
     *
     * @param lat - section latitude coordinate
     * @param lng - section longitude coordinate
     * @return true if latitude and longitude are inside a known section and a parking
     * spot was marked as occupied, false otherwise.
     */
    boolean abandonSection(double lat, double lng);
}
