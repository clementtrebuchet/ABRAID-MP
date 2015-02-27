package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import com.vividsolutions.jts.geom.Point;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for LocationServiceImpl.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class LocationServiceTest {
    private LocationService locationService;
    private CountryDao countryDao;
    private HealthMapCountryDao healthMapCountryDao;
    private LocationDao locationDao;
    private GeoNamesLocationPrecisionDao geoNamesLocationPrecisionDao;
    private GeoNameDao geoNameDao;
    private AdminUnitQCDao adminUnitQCDao;
    private NativeSQL nativeSQL;
    private LandSeaBorderDao landSeaBorderDao;

    @Before
    public void setUp() {
        countryDao = mock(CountryDao.class);
        healthMapCountryDao = mock(HealthMapCountryDao.class);
        locationDao = mock(LocationDao.class);
        geoNamesLocationPrecisionDao = mock(GeoNamesLocationPrecisionDao.class);
        geoNameDao = mock(GeoNameDao.class);
        adminUnitQCDao = mock(AdminUnitQCDao.class);
        nativeSQL = mock(NativeSQL.class);
        landSeaBorderDao = mock(LandSeaBorderDao.class);
        locationService = new LocationServiceImpl(countryDao, healthMapCountryDao, locationDao,
                geoNamesLocationPrecisionDao, geoNameDao, adminUnitQCDao, nativeSQL, landSeaBorderDao);
    }

    @Test
    public void getByPointAndPrecision() {
        // Arrange
        Point point = GeometryUtils.createPoint(1, 2);
        LocationPrecision precision = LocationPrecision.ADMIN1;
        List<Location> locations = Arrays.asList(new Location());
        when(locationDao.getByPointAndPrecision(point, precision)).thenReturn(locations);

        // Act
        List<Location> testLocations = locationService.getLocationsByPointAndPrecision(point, precision);

        // Assert
        assertThat(testLocations).isSameAs(locations);
    }

    @Test
    public void getAllCountries() {
        // Arrange
        List<Country> countries = Arrays.asList(new Country());
        when(countryDao.getAll()).thenReturn(countries);

        // Act
        List<Country> testCountries = locationService.getAllCountries();

        // Assert
        assertThat(testCountries).isSameAs(countries);
    }

    @Test
    public void getAllHealthMapCountries() {
        // Arrange
        List<HealthMapCountry> countries = Arrays.asList(new HealthMapCountry());
        when(healthMapCountryDao.getAll()).thenReturn(countries);

        // Act
        List<HealthMapCountry> testCountries = locationService.getAllHealthMapCountries();

        // Assert
        assertThat(testCountries).isSameAs(countries);
    }

    @Test
    public void getGeoNamesLocationPrecisionMappings() {
        // Arrange
        List<GeoNamesLocationPrecision> precisionList = new ArrayList<>();
        precisionList.add(new GeoNamesLocationPrecision("ADM1", LocationPrecision.ADMIN1));
        precisionList.add(new GeoNamesLocationPrecision("ADM2", LocationPrecision.ADMIN2));
        precisionList.add(new GeoNamesLocationPrecision("PPL", LocationPrecision.PRECISE));
        precisionList.add(new GeoNamesLocationPrecision("PCLI", LocationPrecision.COUNTRY));

        Map<String, LocationPrecision> precisionMap = new HashMap<>();
        precisionMap.put("ADM2", LocationPrecision.ADMIN2);
        precisionMap.put("PPL", LocationPrecision.PRECISE);
        precisionMap.put("ADM1", LocationPrecision.ADMIN1);
        precisionMap.put("PCLI", LocationPrecision.COUNTRY);

        when(geoNamesLocationPrecisionDao.getAll()).thenReturn(precisionList);

        // Act
        Map<String, LocationPrecision> testPrecisionMap = locationService.getGeoNamesLocationPrecisionMappings();

        // Assert
        assertThat(testPrecisionMap).isEqualTo(precisionMap);
    }

    @Test
    public void getAllAdminUnits() {
        // Arrange
        List<AdminUnitQC> adminUnits = Arrays.asList(new AdminUnitQC());
        when(adminUnitQCDao.getAll()).thenReturn(adminUnits);

        // Act
        List<AdminUnitQC> testAdminUnits = locationService.getAllAdminUnitQCs();

        // Assert
        assertThat(testAdminUnits).isSameAs(adminUnits);
    }

    @Test
    public void getAllLandSeaBorders() {
        // Arrange
        List<LandSeaBorder> landSeaBorders = Arrays.asList(new LandSeaBorder());
        when(landSeaBorderDao.getAll()).thenReturn(landSeaBorders);

        // Act
        List<LandSeaBorder> testLandSeaBorders = locationService.getAllLandSeaBorders();

        // Assert
        assertThat(testLandSeaBorders).isSameAs(landSeaBorders);
    }

    @Test
    public void findAdminUnitGlobalThatContainsPoint() {
        // Arrange
        Point point = GeometryUtils.createPoint(1, 2);
        Integer expectedGaulCode = 123;
        when(nativeSQL.findAdminUnitThatContainsPoint(point, true)).thenReturn(expectedGaulCode);

        // Act
        Integer actualGaulCode = locationService.findAdminUnitGlobalThatContainsPoint(point);

        // Assert
        assertThat(actualGaulCode).isEqualTo(expectedGaulCode);
    }

    @Test
    public void findAdminUnitTropicalThatContainsPoint() {
        // Arrange
        Point point = GeometryUtils.createPoint(1, 2);
        Integer expectedGaulCode = 123;
        when(nativeSQL.findAdminUnitThatContainsPoint(point, false)).thenReturn(expectedGaulCode);

        // Act
        Integer actualGaulCode = locationService.findAdminUnitTropicalThatContainsPoint(point);

        // Assert
        assertThat(actualGaulCode).isEqualTo(expectedGaulCode);
    }

    @Test
    public void findCountryThatContainsPoint() {
        // Arrange
        Point point = GeometryUtils.createPoint(1, 2);
        Integer expectedGaulCode = 123;
        when(nativeSQL.findCountryThatContainsPoint(point)).thenReturn(expectedGaulCode);

        // Act
        Integer actualGaulCode = locationService.findCountryThatContainsPoint(point);

        // Assert
        assertThat(actualGaulCode).isEqualTo(expectedGaulCode);
    }

    @Test
    public void doesLandSeaBorderContainPoint() {
        // Arrange
        Point point = GeometryUtils.createPoint(1, 2);
        when(nativeSQL.doesLandSeaBorderContainPoint(point)).thenReturn(true);

        // Act
        boolean result = locationService.doesLandSeaBorderContainPoint(point);

        // Assert
        assertThat(result).isTrue();
    }
}
