package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import com.fasterxml.jackson.annotation.JsonView;
import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitDiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitGlobalOrTropical;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitReview;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.views.DisplayJsonView;

import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * A DTO for the properties of an AdminUnit, with reference to a DiseaseGroup.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonDiseaseExtentFeatureProperties {
    @JsonView(DisplayJsonView.class)
    private String name;

    @JsonView(DisplayJsonView.class)
    private String diseaseExtentClass;

    @JsonView(DisplayJsonView.class)
    private Integer occurrenceCount;

    @JsonView(DisplayJsonView.class)
    private boolean needsReview;

    public GeoJsonDiseaseExtentFeatureProperties(AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass,
                                                 List<AdminUnitReview> reviews)
    {
        setName(adminUnitDiseaseExtentClass.getAdminUnitGlobalOrTropical().getPublicName());
        setDiseaseExtentClass(formatDisplayString(adminUnitDiseaseExtentClass.getDiseaseExtentClass().getName()));
        setOccurrenceCount(adminUnitDiseaseExtentClass.getOccurrenceCount());
        setNeedsReview(computeNeedsReview(adminUnitDiseaseExtentClass, reviews));
    }

    private String formatDisplayString(String s) {
        s = s.replace("_", " ");
        return s.charAt(0) + s.substring(1).toLowerCase();
    }

    private boolean computeNeedsReview(AdminUnitDiseaseExtentClass extentClass, List<AdminUnitReview> reviews) {
        DateTime extentClassChangedDate = extentClass.getClassChangedDate();
        if (extentClassChangedDate == null) {
            // Extent class has not changed since last disease extent generation, so does not need review
            return false;
        } else {
            DateTime reviewedDate = extractReviewedDate(reviews, extentClass.getAdminUnitGlobalOrTropical());
            return (reviewedDate == null || extentClassChangedDate.isAfter(reviewedDate));
            // Needs review if expert has never reviewed it previously, or if class has changed since last review.
        }
    }

    private DateTime extractReviewedDate(List<AdminUnitReview> reviews, AdminUnitGlobalOrTropical adminUnit) {
        List<AdminUnitReview> reviewsOfAdminUnit = select(reviews,
            having(on(AdminUnitReview.class).getAdminUnitGlobalOrTropicalGaulCode(), equalTo(adminUnit.getGaulCode())));
        return max(reviewsOfAdminUnit, on(AdminUnitReview.class).getCreatedDate());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiseaseExtentClass() {
        return diseaseExtentClass;
    }

    public void setDiseaseExtentClass(String diseaseExtentClass) {
        this.diseaseExtentClass = diseaseExtentClass;
    }

    public Integer getOccurrenceCount() {
        return occurrenceCount;
    }

    public void setOccurrenceCount(Integer occurrenceCount) {
        this.occurrenceCount = occurrenceCount;
    }

    /**
     * Getter for boolean property needsReview.
     * @return needsReview.
     */
    public boolean needsReview() {
        return needsReview;
    }

    public void setNeedsReview(boolean needsReview) {
        this.needsReview = needsReview;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeoJsonDiseaseExtentFeatureProperties)) return false;

        GeoJsonDiseaseExtentFeatureProperties that = (GeoJsonDiseaseExtentFeatureProperties) o;

        if (diseaseExtentClass != null ? !diseaseExtentClass.equals(that.diseaseExtentClass) : that.diseaseExtentClass != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (diseaseExtentClass != null ? diseaseExtentClass.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
