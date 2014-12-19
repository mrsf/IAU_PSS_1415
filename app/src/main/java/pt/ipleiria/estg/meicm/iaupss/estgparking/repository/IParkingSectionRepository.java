package pt.ipleiria.estg.meicm.iaupss.estgparking.repository;

import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.model.ParkingSection;

/**
 * Created by francisco on 19-12-2014.
 */
public interface IParkingSectionRepository {

    List<ParkingSection> fetchParkingSections();
}
