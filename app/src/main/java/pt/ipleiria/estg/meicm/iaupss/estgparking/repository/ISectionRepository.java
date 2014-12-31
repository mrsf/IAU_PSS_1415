package pt.ipleiria.estg.meicm.iaupss.estgparking.repository;

import java.util.List;

import pt.ipleiria.estg.meicm.iaupss.estgparking.model.Section;

public interface ISectionRepository {

    List<Section> fetchSections();
}
