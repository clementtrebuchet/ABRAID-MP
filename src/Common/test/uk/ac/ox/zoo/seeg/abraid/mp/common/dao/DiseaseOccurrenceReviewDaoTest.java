package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the DiseaseOccurrenceReviewDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceReviewDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private AlertDao alertDao;

    @Autowired
    private DiseaseGroupDao diseaseGroupDao;

    @Autowired
    private DiseaseOccurrenceDao diseaseOccurrenceDao;

    @Autowired
    private DiseaseOccurrenceReviewDao diseaseOccurrenceReviewDao;

    @Autowired
    private ExpertDao expertDao;

    @Autowired
    private FeedDao feedDao;

    @Autowired
    private LocationDao locationDao;

    @Test
    public void getCountByExpertId() {
        // Arrange - no reviews in the database

        // Act
        Long count = diseaseOccurrenceReviewDao.getCountByExpertId(1);

        // Assert
        assertThat(count).isEqualTo(0);
    }

    @Test
    public void doesDiseaseOccurrenceReviewExistReturnsTrueWhenExpected() {
        // Arrange
        Expert expert = createExpert();
        DiseaseOccurrence occurrence = createDiseaseOccurrence();
        createDiseaseOccurrenceReview(expert, occurrence);

        // Act
        boolean result = diseaseOccurrenceReviewDao.doesDiseaseOccurrenceReviewExist(expert.getId(), occurrence.getId());

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void doesDiseaseOccurrenceReviewExistReturnsFalseWhenReviewDoesNotExist() {
        // Arrange
        Expert expert = createExpert();
        DiseaseOccurrence occurrence = createDiseaseOccurrence();

        // Act
        boolean result = diseaseOccurrenceReviewDao.doesDiseaseOccurrenceReviewExist(expert.getId(), occurrence.getId());

        // Assert
        assertThat(result).isFalse();
    }

    private DiseaseOccurrenceReview createDiseaseOccurrenceReview(Expert expert, DiseaseOccurrence occurrence) {
        DiseaseOccurrenceReview review = new DiseaseOccurrenceReview();
        review.setExpert(expert);
        review.setDiseaseOccurrence(occurrence);
        review.setResponse(DiseaseOccurrenceReviewResponse.YES);
        diseaseOccurrenceReviewDao.save(review);
        return review;
    }

    private Expert createExpert() {
        String name = "Test Expert";
        String email = "expert@test.com";
        String password = "pa55word";
        String jobTitle = "job";
        String institution = "institution";
        boolean visibilityRequested = true;

        Expert expert = new Expert();
        expert.setName(name);
        expert.setEmail(email);
        expert.setPassword(password);
        expert.setJobTitle(jobTitle);
        expert.setInstitution(institution);
        //noinspection ConstantConditions
        expert.setVisibilityRequested(visibilityRequested);
        expertDao.save(expert);

        return expert;
    }

    private DiseaseOccurrence createDiseaseOccurrence() {
        DiseaseOccurrence diseaseOccurrence = new DiseaseOccurrence();
        diseaseOccurrence.setAlert(createAlert());
        diseaseOccurrence.setDiseaseGroup(createDiseaseGroup());
        diseaseOccurrence.setLocation(createLocation());
        diseaseOccurrence.setOccurrenceDate(DateTime.now());
        diseaseOccurrence.setStatus(DiseaseOccurrenceStatus.READY);
        diseaseOccurrenceDao.save(diseaseOccurrence);
        return diseaseOccurrence;
    }

    private Alert createAlert() {
        Feed feed = feedDao.getById(1);
        DateTime publicationDate = DateTime.now().minusDays(5);
        int healthMapAlertId = 100;
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
        String placeName = "Oxford";
        double x = 51.75042;
        double y = -1.24759;

        Location location = new Location();
        location.setName(placeName);
        location.setGeom(x, y);
        location.setPrecision(LocationPrecision.PRECISE);
        location.setResolutionWeighting(1.0);
        locationDao.save(location);

        return location;
    }
}
