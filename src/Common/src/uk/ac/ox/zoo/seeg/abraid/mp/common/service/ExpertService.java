package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.List;

/**
 * Service interface for experts.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface ExpertService {
    /**
     * Gets a list of all experts.
     * @return A list of all experts.
     */
    List<Expert> getAllExperts();

    /**
     * Gets an expert by email address.
     * @param email The email address.
     * @return The expert, or null if not found.
     * @throws org.springframework.dao.DataAccessException if multiple experts with this email address are found
     * (should not occur as emails are unique)
     */
    Expert getExpertByEmail(String email);

    /**
     * Gets the specified expert's disease interests.
     *
     * @param expertId The id of the specified expert.
     * @return The list of validator disease groups the expert can validate.
     */
    List<ValidatorDiseaseGroup> getDiseaseInterests(Integer expertId);

    /**
     * Gets a list of occurrence points, for the specified validator disease group, for which the specified expert has
     * not yet submitted a review.
     * @param expertId The id of the specified expert.
     * @param validatorDiseaseGroupId The id of the validatorDiseaseGroup of interest.
     * @return The list of disease occurrence points to be displayed to the expert on the map.
     * @throws java.lang.IllegalArgumentException if the expertId or validatorDiseaseGroupId cannot be found in the
     * database.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesYetToBeReviewedByExpert(Integer expertId,
                                                                         Integer validatorDiseaseGroupId)
            throws IllegalArgumentException;

    /**
     * Gets the number of disease occurrence reviews an expert has submitted, across all disease groups.
     * @param expertId The id of the specified expert.
     * @return The total number of disease occurrence reviews for the specified expert.
     */
    Long getDiseaseOccurrenceReviewCount(Integer expertId);

    /**
     * Determines whether a review for the specified disease occurrence, by the specified expert,
     * already exists in the database.
     * @param expertId The id of the specified expert.
     * @param diseaseOccurrenceId The id of the disease occurrence.
     * @return True if the review already exists, otherwise false.
     */
    boolean doesDiseaseOccurrenceReviewExist(Integer expertId, Integer diseaseOccurrenceId);

    /**
     * Gets the review, defined by the unique triplet of input arguments, if it exists in the database.
     * @param expertId The id of the expert.
     * @param diseaseGroupId The id of the disease group.
     * @param gaulCode The gaulCode of the administrative unit.
     * @return The adminUnitReview if it exists, otherwise null.
     */
    AdminUnitReview getAdminUnitReview(Integer expertId, Integer diseaseGroupId, Integer gaulCode);

    /**
     * Gets all reviews for the specified disease group.
     * @param diseaseGroupId The id of the disease group.
     * @return A list of reviews.
     */
    List<AdminUnitReview> getAllAdminUnitReviewsForDiseaseGroup(Integer diseaseGroupId);

    /**
     * Gets all reviews submitted by the specified expert, for the specified disease group.
     * @param expertId The id of the specified expert.
     * @param diseaseGroupId The id of the disease group.
     * @return A list of reviews.
     */
    List<AdminUnitReview> getAllAdminUnitReviewsForDiseaseGroup(Integer expertId, Integer diseaseGroupId);

    /**
     * Gets the number of admin unit reviews an expert has submitted, across all disease groups.
     * @param expertId The id of the specified expert.
     * @return The total number of admin unit reviews for the specified expert.
     */
    Long getAdminUnitReviewCount(Integer expertId);

    /**
     * Saves the disease occurrence review.
     * @param expertEmail The email address of the expert providing review.
     * @param occurrenceId The id of the disease occurrence.
     * @param response The expert's response.
     */
    void saveDiseaseOccurrenceReview(String expertEmail, Integer occurrenceId,
                                     DiseaseOccurrenceReviewResponse response);

    /**
     * Saves the review of the administrative unit.
     * @param expertEmail The email address of the expert providing review.
     * @param diseaseGroupId The id of the disease group.
     * @param gaulCode The gaulCode of the administrative unit.
     * @param response The expert's response.
     */
    void saveNewAdminUnitReview(String expertEmail, Integer diseaseGroupId, Integer gaulCode,
                                DiseaseExtentClass response);

    /**
     * Saves the updated review of the administrative unit (with a new disease extent class, and new changed date).
     * @param review The existing AdminUnitReview from the database.
     * @param response The expert's new response.
     */
    void updateExistingAdminUnitReview(AdminUnitReview review, DiseaseExtentClass response);

    /**
     * Saves the specified expert.
     * @param expert The expert to save.
     */
    void saveExpert(Expert expert);
}
