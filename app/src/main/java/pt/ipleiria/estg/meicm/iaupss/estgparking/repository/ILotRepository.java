package pt.ipleiria.estg.meicm.iaupss.estgparking.repository;

import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Lot;

public interface ILotRepository {

    List<Lot> fetchLots();

    /**
     * Finds the parking lot where the given coordinates lie within
     *
     * @param lat - lot latitude coordinate
     * @param lng - lot longitude coordinate
     * @return The parking lot Id if found, null otherwise.
     */
    String findLot(double lat, double lng);
}
