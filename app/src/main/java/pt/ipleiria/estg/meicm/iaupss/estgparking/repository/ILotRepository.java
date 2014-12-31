package pt.ipleiria.estg.meicm.iaupss.estgparking.repository;

import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Lot;

public interface ILotRepository {

    List<Lot> fetchLots();
}
