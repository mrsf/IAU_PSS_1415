package pt.ipleiria.estg.meicm.iaupss.estgparking.repository;

import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.model.UserRanking;

public interface IUserRankingRepository {

    List<UserRanking> fetchUserRankings();
}
