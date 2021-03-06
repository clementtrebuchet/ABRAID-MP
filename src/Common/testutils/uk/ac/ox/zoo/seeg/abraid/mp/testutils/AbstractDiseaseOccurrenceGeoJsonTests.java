package uk.ac.ox.zoo.seeg.abraid.mp.testutils;

import com.vividsolutions.jts.geom.Point;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Base class to ease the setup of mocks in disease occurrence GeoJSON tests.
 * Copyright (c) 2014 University of Oxford
 */
public abstract class AbstractDiseaseOccurrenceGeoJsonTests {
    public static Alert mockAlert(String title, String summary, String feedName, String feedLanguage, String url) {
        Alert alert = mock(Alert.class);
        Feed feed = mock(Feed.class);
        when(alert.getTitleForDisplay()).thenReturn(title);
        when(alert.getSummary()).thenReturn(summary);
        when(alert.getUrl()).thenReturn(url);
        when(alert.getFeed()).thenReturn(feed);
        when(feed.getName()).thenReturn(feedName);
        when(feed.getLanguage()).thenReturn(feedLanguage);
        return alert;
    }

    public static Alert defaultAlert() {
        return mockAlert("title", "summary", "feedName", "feedLanguage", "url");
    }

    public static Location mockLocation(double longitude, double latitude, String locationName, LocationPrecision locationPrecision, int adminUnitQCGaulCode, int countryGaulCode) {
        Location location = mock(Location.class);
        Point geom = mock(Point.class);
        when(location.getGeom()).thenReturn(geom);
        when(geom.getX()).thenReturn(latitude);
        when(geom.getY()).thenReturn(longitude);
        when(location.getName()).thenReturn(locationName);
        when(location.getPrecision()).thenReturn(locationPrecision);
        when(location.getAdminUnitQCGaulCode()).thenReturn(adminUnitQCGaulCode);
        when(location.getCountryGaulCode()).thenReturn(countryGaulCode);
        return location;
    }

    public static Location defaultLocation() {
        return mockLocation(1.0, -1.0, "locationName", LocationPrecision.ADMIN1, 102, 201);
    }

    public static DiseaseOccurrence mockDiseaseOccurrence(int id, DiseaseGroup diseaseGroup, Location location, DateTime occurrenceDate, Alert alert, double weighting) {
        DiseaseOccurrence occurrence = mock(DiseaseOccurrence.class);
        when(occurrence.getId()).thenReturn(id);
        when(occurrence.getDiseaseGroup()).thenReturn(diseaseGroup);
        when(occurrence.getLocation()).thenReturn(location);
        when(occurrence.getOccurrenceDate()).thenReturn(occurrenceDate);
        when(occurrence.getAlert()).thenReturn(alert);
        when(occurrence.getFinalWeighting()).thenReturn(weighting);
        return occurrence;
    }

    public static DiseaseOccurrence defaultDiseaseOccurrence() {
        return mockDiseaseOccurrence(1, defaultDiseaseGroup(), defaultLocation(), (new DateTime(0)).withZone(DateTimeZone.UTC), defaultAlert(), 0.5);
    }

    public static DiseaseGroup defaultDiseaseGroup() {
        return mockDiseaseGroup("diseaseGroupPublicName", true, 123);
    }

    private static DiseaseGroup mockDiseaseGroup(String diseaseGroupPublicName, boolean isGlobal, int diseaseId) {
        DiseaseGroup mockDiseaseGroup = mock(DiseaseGroup.class);
        when(mockDiseaseGroup.getId()).thenReturn(diseaseId);
        when(mockDiseaseGroup.getPublicNameForDisplay()).thenReturn(diseaseGroupPublicName);
        when(mockDiseaseGroup.isGlobal()).thenReturn(isGlobal);
        return mockDiseaseGroup;
    }

    public static String getTwoDiseaseOccurrenceFeaturesAsJson() {

        String displayViewProperties =
           "            \"diseaseGroupPublicName\":\"diseaseGroupPublicName\"," +
           "            \"locationName\":\"locationName\"," +
           "            \"alert\":{" +
           "               \"title\":\"title\"," +
           "               \"summary\":\"summary\"," +
           "               \"url\":\"url\"," +
           "               \"feedName\":\"feedName\"," +
           "               \"feedLanguage\":\"feedLanguage\"" +
           "            }," +
           "            \"occurrenceDate\":\"" + ISODateTimeFormat.dateTime().withZoneUTC().print(new DateTime(0)) + "\"";

        return (
            "{" +
                    "   \"type\":\"FeatureCollection\"," +
                    "   \"crs\":{" +
                    "      \"type\":\"name\"," +
                    "      \"properties\":{" +
                    "         \"name\":\"urn:ogc:def:crs:EPSG::4326\"" +
                    "      }" +
                    "   }," +
                    "   \"features\":[" +
                    "      {" +
                    "         \"type\":\"Feature\"," +
                    "         \"id\":1," +
                    "         \"geometry\":{" +
                    "            \"type\":\"Point\"," +
                    "            \"coordinates\":[" +
                    "               -1.0," +
                    "               1.0" +
                    "            ]" +
                    "         }," +
                    "         \"properties\":{" +
                    displayViewProperties +
                    "         }" +
                    "      }," +
                    "      {" +
                    "         \"type\":\"Feature\"," +
                    "         \"id\":1," +
                    "         \"geometry\":{" +
                    "            \"type\":\"Point\"," +
                    "            \"coordinates\":[" +
                    "               -1.0," +
                    "               1.0" +
                    "            ]" +
                    "         }," +
                    "         \"properties\":{" +
                    displayViewProperties +
                    "         }" +
                    "      }" +
                    "   ]" +
                    "}").replaceAll(" ", "");
    }
}
