package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;

import java.util.List;

/**
 * Interface for the Expert entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface ExpertDao {
    /**
     * Gets all experts.
     * @return A list of all experts.
     */
    List<Expert> getAll();

    /**
     * Gets an expert by name.
     * @param name The name.
     * @return The expert, or null if not found.
     * @throws org.springframework.dao.DataAccessException if multiple experts with this name are found (should not
     * occur as names are unique)
     */
    Expert getByName(String name);

    /**
     * Saves the specified expert.
     * @param expert The expert to save.
     */
    void save(Expert expert);

}
