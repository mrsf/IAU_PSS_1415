package pt.ipleiria.estg.meicm.iaupss.estgparking.repository;

import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.model.ParkingLot;

/**
 * Created by francisco on 05-12-2014.
 */
public interface IParkingLotRepository {

    List<ParkingLot> fetchParkingLots();
}
