package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Provenance;

/**
 * The Provenance entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Repository
public class ProvenanceDaoImpl extends AbstractDao<Provenance, Integer> implements ProvenanceDao {
    /**
     * Gets a provenance by name.
     *
     * @param name The provenance name
     * @return The provenance name, or null if it does not exist.
     */
    @Override
    public Provenance getByName(String name) {
        Query query = namedQuery("getProvenanceByName");
        query.setString("name", name);
        return uniqueResult(query);
    }
}
