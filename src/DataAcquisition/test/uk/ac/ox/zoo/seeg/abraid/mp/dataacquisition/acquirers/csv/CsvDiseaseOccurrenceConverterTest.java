package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.DataAcquisitionException;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv.domain.CsvDiseaseOccurrence;

import java.util.HashMap;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the CsvDiseaseOccurrenceConverter class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class CsvDiseaseOccurrenceConverterTest {
    private CsvDiseaseOccurrenceConverter converter;

    @Before
    public void setUp() {
        CsvLookupData csvLookupData = setUpCsvLookupData();
        converter = new CsvDiseaseOccurrenceConverter(csvLookupData);
    }

    private CsvLookupData setUpCsvLookupData() {
        HashMap<String, Country> countryMap = new HashMap<>();
        countryMap.put("france", new Country(85, "France"));
        countryMap.put("venezuela", new Country(263, "Venezuela"));

        HashMap<String, DiseaseGroup> diseaseGroupMap = new HashMap<>();
        diseaseGroupMap.put("dengue", new DiseaseGroup(87, "Dengue"));
        diseaseGroupMap.put("malarias", new DiseaseGroup(202, "Malarias"));

        Feed feed = new Feed("Uploaded");

        CsvLookupData csvLookupData = mock(CsvLookupData.class);
        when(csvLookupData.getCountryMap()).thenReturn(countryMap);
        when(csvLookupData.getDiseaseGroupMap()).thenReturn(diseaseGroupMap);
        when(csvLookupData.getFeedForUploadedData()).thenReturn(feed);
        return csvLookupData;
    }

    @Test
    public void convertSucceedsForValidInput() {
        // Arrange
        CsvDiseaseOccurrence csvDiseaseOccurrence = createCsvDiseaseOccurrence();

        // Act
        DiseaseOccurrence occurrence = converter.convert(csvDiseaseOccurrence);

        // Assert
        Location location = occurrence.getLocation();
        assertThat(location).isNotNull();
        assertThat(location.getName()).isEqualTo("Paris");
        assertThat(location.getGeom().getX()).isEqualTo(2.3508);
        assertThat(location.getGeom().getY()).isEqualTo(48.8567);
        assertThat(location.getPrecision()).isEqualTo(LocationPrecision.PRECISE);
        assertThat(location.getCountryGaulCode()).isEqualTo(85);

        Alert alert = occurrence.getAlert();
        assertThat(alert).isNotNull();
        assertThat(alert.getFeed()).isNotNull();
        assertThat(alert.getFeed().getName()).isEqualTo("Uploaded");
        assertThat(alert.getTitle()).isEqualTo("Disease occurrence title");
        assertThat(alert.getSummary()).isEqualTo("Disease occurrence summary");
        assertThat(alert.getUrl()).isEqualTo("http://testurl.com");

        DiseaseGroup diseaseGroup = occurrence.getDiseaseGroup();
        assertThat(diseaseGroup).isNotNull();
        assertThat(diseaseGroup.getId()).isEqualTo(87);
        assertThat(diseaseGroup.getName()).isEqualTo("Dengue");
        assertEqual(occurrence.getOccurrenceDate(), "2014-09-10T00:00:00Z");
    }

    @Test
    public void convertFailsIfValidatorFails() {
        // Arrange
        CsvDiseaseOccurrence csvDiseaseOccurrence = createCsvDiseaseOccurrence();
        csvDiseaseOccurrence.setSite(null);

        // Act and assert
        expectFailure(csvDiseaseOccurrence, "Site is missing");
    }

    @Test
    public void convertFailsIfDiseaseGroupNameIsInvalid() {
        // Arrange
        CsvDiseaseOccurrence csvDiseaseOccurrence = createCsvDiseaseOccurrence();
        csvDiseaseOccurrence.setDiseaseGroupName("Tonsillitis");

        // Act and assert
        expectFailure(csvDiseaseOccurrence, "Disease group name \"Tonsillitis\" is invalid");
    }

    @Test
    public void convertSucceedsForMonthYearOccurrenceDate() {
        // Arrange
        CsvDiseaseOccurrence csvDiseaseOccurrence = createCsvDiseaseOccurrence();
        csvDiseaseOccurrence.setOccurrenceDate("05/2013");

        // Act
        DiseaseOccurrence occurrence = converter.convert(csvDiseaseOccurrence);

        // Assert
        assertEqual(occurrence.getOccurrenceDate(), "2013-05-01T00:00:00Z");
    }

    @Test
    public void convertSucceedsForYearOccurrenceDate() {
        // Arrange
        CsvDiseaseOccurrence csvDiseaseOccurrence = createCsvDiseaseOccurrence();
        csvDiseaseOccurrence.setOccurrenceDate("2012");

        // Act
        DiseaseOccurrence occurrence = converter.convert(csvDiseaseOccurrence);

        // Assert
        assertEqual(occurrence.getOccurrenceDate(), "2012-01-01T00:00:00Z");
    }

    @Test
    public void convertFailsForInvalidOccurrenceDate() {
        // Arrange
        CsvDiseaseOccurrence csvDiseaseOccurrence = createCsvDiseaseOccurrence();
        csvDiseaseOccurrence.setOccurrenceDate("01/13/2013");

        // Act and assert
        expectFailure(csvDiseaseOccurrence,
                "Occurrence date \"01/13/2013\" is invalid (valid formats are dd/MM/YYYY, MM/YYYY, YYYY)");
    }

    @Test
    public void convertFailsForInvalidPrecision() {
        // Arrange
        CsvDiseaseOccurrence csvDiseaseOccurrence = createCsvDiseaseOccurrence();
        csvDiseaseOccurrence.setPrecision("  Street");

        // Act and assert
        expectFailure(csvDiseaseOccurrence, "Location precision \"Street\" is invalid");
    }

    @Test
    public void convertFailsForInvalidCountryName() {
        // Arrange
        CsvDiseaseOccurrence csvDiseaseOccurrence = createCsvDiseaseOccurrence();
        csvDiseaseOccurrence.setCountryName("Outer Mongolia");

        // Act and assert
        expectFailure(csvDiseaseOccurrence, "Country name \"Outer Mongolia\" is invalid");
    }

    private void expectFailure(CsvDiseaseOccurrence csvDiseaseOccurrence, String message) {
        catchException(converter).convert(csvDiseaseOccurrence);
        assertThat(caughtException()).isInstanceOf(DataAcquisitionException.class);
        assertThat(caughtException()).hasMessage(message);
    }

    private CsvDiseaseOccurrence createCsvDiseaseOccurrence() {
        CsvDiseaseOccurrence occurrence = new CsvDiseaseOccurrence();
        occurrence.setSite(" Paris ");
        occurrence.setLongitude(2.3508);
        occurrence.setLatitude(48.8567);
        occurrence.setPrecision(LocationPrecision.PRECISE.name());
        occurrence.setCountryName("France");
        occurrence.setDiseaseGroupName("DENGUE");
        occurrence.setOccurrenceDate("10/09/2014");
        occurrence.setTitle("Disease occurrence title");
        occurrence.setSummary("Disease occurrence summary");
        occurrence.setUrl("http://testurl.com");
        return occurrence;
    }

    private void assertEqual(DateTime actualDateTime, String expectedDateTimeISOFormat) {
        long expectedMillis = new DateTime(expectedDateTimeISOFormat).getMillis();
        assertThat(actualDateTime.getMillis()).isEqualTo(expectedMillis);
    }
}