package pt.ipleiria.estg.meicm.iaupss.estgparking.repository;

import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.model.ParkingSection;

public interface IParkingSectionRepository {

    List<ParkingSection> fetchParkingSections();
}
