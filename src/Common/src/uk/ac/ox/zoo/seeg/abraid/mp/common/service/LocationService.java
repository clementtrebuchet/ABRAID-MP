package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import com.vividsolutions.jts.geom.Point;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.List;
import java.util.Map;

/**
 * Service interface for locations, including countries.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface LocationService {
    /**
     * Gets a location by GeoNames ID.
     * @param geoNamesId The GeoNames ID.
     * @return The location with the specified GeoNames ID, or null if not found.
     */
    Location getLocationByGeoNamesId(int geoNamesId);

    /**
     * Gets a list of locations that have the specified point and precision. This returns a list of locations as there
     * may be several at the same point with the same precision.
     * @param point The point.
     * @param precision The precision.
     * @return The locations at this point. If none is found, the list is empty.
     */
    List<Location> getLocationsByPointAndPrecision(Point point, LocationPrecision precision);

    /**
     * Gets all countries.
     * @return All countries.
     */
    List<Country> getAllCountries();

    /**
     * Gets all HealthMap countries.
     * @return All HealthMap countries.
     */
    List<HealthMapCountry> getAllHealthMapCountries();

    /**
     * Gets mappings between GeoNames feature codes and location precision.
     * @return A set of mappings.
     */
    Map<String, LocationPrecision> getGeoNamesLocationPrecisionMappings();
}
