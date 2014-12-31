package pt.ipleiria.estg.meicm.iaupss.estgparking.repository;

import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Ranking;

public interface IUserRankingRepository {

    List<Ranking> fetchUserRankings();
}
