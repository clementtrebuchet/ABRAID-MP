package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import java.util.List;

/**
 * Interface for the ModelRun entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface ModelRunDao {
    /**
     * Gets all model runs.
     * @return All model runs.
     */
    List<ModelRun> getAll();

    /**
     * Gets a model run by name.
     * @param name The model run name.
     * @return The model run with the specified name, or null if it does not exist.
     */
    ModelRun getByName(String name);

    /**
     * Saves the specified model run.
     * @param modelRun The model run to save.
     */
    void save(ModelRun modelRun);
}