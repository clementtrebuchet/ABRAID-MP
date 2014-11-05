package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateInfluence;

/**
 * A DTO to represent the covariate associated with a model run.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonCovariateInfluence extends AbstractJsonCovariateInfluence {
    public JsonCovariateInfluence(CovariateInfluence covariateInfluence) {
        super(covariateInfluence);
    }
}