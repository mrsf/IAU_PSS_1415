package pt.ipleiria.estg.meicm.iaupss.estgparking.repository;

import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.model.ParkingLot;

public interface IParkingLotRepository {

    List<ParkingLot> fetchParkingLots();
}
