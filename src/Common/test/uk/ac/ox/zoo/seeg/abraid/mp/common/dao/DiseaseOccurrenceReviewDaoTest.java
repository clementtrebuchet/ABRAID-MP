package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import com.vividsolutions.jts.geom.Point;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractSpringUnitTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;


/**
 * Tests the DiseaseOccurrenceReviewDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceReviewDaoTest extends AbstractSpringIntegrationTests {
    @Autowired
    private DiseaseOccurrenceReviewDao diseaseOccurrenceReviewDao;
    @Autowired
    private ExpertDao expertDao;
    @Autowired
    private FeedDao feedDao;
    @Autowired
    private AlertDao alertDao;
    @Autowired
    private DiseaseGroupDao diseaseGroupDao;
    @Autowired
    private DiseaseOccurrenceDao diseaseOccurrenceDao;
    @Autowired
    private CountryDao countryDao;
    @Autowired
    private LocationDao locationDao;

    @Test
    public void getAllReviewsForExpert() {
        // Arrange
        Expert expert = createExpert();
        DiseaseOccurrence diseaseOccurrence = createDiseaseOccurrence();
        DiseaseOccurrenceReviewResponse response = DiseaseOccurrenceReviewResponse.YES;

        DiseaseOccurrenceReview diseaseOccurrenceReview = new DiseaseOccurrenceReview();
        diseaseOccurrenceReview.setExpert(expert);
        diseaseOccurrenceReview.setDiseaseOccurrence(diseaseOccurrence);
        diseaseOccurrenceReview.setResponse(response);

        // Act
        diseaseOccurrenceReviewDao.save(diseaseOccurrenceReview);
        flushAndClear();

        // Assert
        List<DiseaseOccurrenceReview> reviews = diseaseOccurrenceReviewDao.getByExpertId(expert.getId());
        assertThat(reviews).hasSize(1);

        DiseaseOccurrenceReview review = reviews.get(0);
        assertThat(review.getResponse()).isEqualTo(response);
        assertThat(review.getExpert().getEmail()).isEqualTo(expert.getEmail());
        assertThat(review.getDiseaseOccurrence().getId()).isEqualTo(diseaseOccurrence.getId());
    }

    private Expert createExpert() {
        String name = "Test Expert";
        String email = "expert@test.com";
        String password = "pa55word";
        boolean isAdministrator = true;

        Expert expert = new Expert();
        expert.setName(name);
        expert.setEmail(email);
        expert.setPassword(password);
        //noinspection ConstantConditions
        expert.setAdministrator(isAdministrator);
        expertDao.save(expert);

        return expert;
    }

    private DiseaseOccurrence createDiseaseOccurrence() {
        DiseaseOccurrence diseaseOccurrence = new DiseaseOccurrence();
        diseaseOccurrence.setAlert(createAlert());
        diseaseOccurrence.setDiseaseGroup(createDiseaseGroup());
        diseaseOccurrence.setLocation(createLocation());
        diseaseOccurrenceDao.save(diseaseOccurrence);
        return diseaseOccurrence;
    }

    private Alert createAlert() {
        Feed feed = feedDao.getById(1);
        Calendar publicationCalendar = Calendar.getInstance();
        publicationCalendar.add(Calendar.DAY_OF_YEAR, -5);
        Date publicationDate = publicationCalendar.getTime();
        long healthMapAlertId = 100L;
        String title = "Dengue/DHF update (15): Asia, Indian Ocean, Pacific";
        String summary = "This is a summary of the alert";
        String url = "http://www.promedmail.org/direct.php?id=20140217.2283261";

        Alert alert = new Alert();
        alert.setFeed(feed);
        alert.setHealthMapAlertId(healthMapAlertId);
        alert.setPublicationDate(publicationDate);
        alert.setTitle(title);
        alert.setSummary(summary);
        alert.setUrl(url);
        alertDao.save(alert);

        return alert;
    }

    private DiseaseGroup createDiseaseGroup() {
        DiseaseGroup diseaseGroup = new DiseaseGroup();
        diseaseGroup.setName("Test cluster");
        diseaseGroup.setGroupType(DiseaseGroupType.CLUSTER);
        diseaseGroupDao.save(diseaseGroup);
        return diseaseGroup;
    }

    private Location createLocation() {
        String countryName = "UK of Great Britain and Northern Ireland";
        String placeName = "Oxford";
        String admin1 = "England";
        String admin2 = "Oxfordshire";
        Country country = countryDao.getByName(countryName);
        double x = 51.75042;
        double y = -1.24759;
        Point point = GeometryUtils.createPoint(x, y);

        Location location = new Location();
        location.setName(placeName);
        location.setGeom(point);
        location.setPrecision(LocationPrecision.PRECISE);
        location.setCountry(country);
        locationDao.save(location);

        return location;
    }
}
