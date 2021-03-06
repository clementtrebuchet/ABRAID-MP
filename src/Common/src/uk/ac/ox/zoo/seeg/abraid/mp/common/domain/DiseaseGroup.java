package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.util.StringUtils;

import javax.persistence.*;

/**
 * Represents a group of diseases as defined by SEEG. This can be a disease cluster, disease microcluster, or a disease
 * itself.
 *
 * Copyright (c) 2014 University of Oxford
 */
@NamedQueries({
        @NamedQuery(
                name = "getDiseaseGroupIdsForAutomaticModelRuns",
                query = "select id from DiseaseGroup where automaticModelRunsStartDate is not null"
        ),
        @NamedQuery(
                name = "getDiseaseGroupsNeedingOccurrenceReviewByExpert",
                query = "select distinct do.diseaseGroup " +
                        "from DiseaseOccurrence as do " +
                        "where do.status='IN_REVIEW' " +
                        "and do.id not in (" +
                        "   select distinct diseaseOccurrence.id " +
                        "   from DiseaseOccurrenceReview as r " +
                        "   where r.expert.id = :expertId" +
                        ") " +
                        "and do.diseaseGroup.automaticModelRunsStartDate is not null"
        ),
        @NamedQuery(
                name = "getDiseaseGroupsNeedingExtentReviewByExpert",
                query = "select distinct dec.diseaseGroup " +
                        "from AdminUnitDiseaseExtentClass as dec " +
                        "where coalesce(dec.adminUnitGlobal.gaulCode, dec.adminUnitTropical.gaulCode) not in (" +
                        "   select coalesce(r.adminUnitGlobalGaulCode, r.adminUnitGlobalGaulCode) " +
                        "   from AdminUnitReview as r " +
                        "   where r.createdDate >= dec.classChangedDate " +
                        "   and r.diseaseGroup.id=dec.diseaseGroup.id " +
                        "   and r.expert.id=:expertId" +
                        ") " +
                        "and dec.diseaseGroup.automaticModelRunsStartDate is not null"
        ),
        @NamedQuery(
                name = "getDiseaseGroupNamesForHealthMapReport",
                query = "select name from DiseaseGroup where isPriorityDisease is TRUE order by name asc"
        )
})
@Entity
@Table(name = "disease_group")
public class DiseaseGroup {
    // The primary key.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // The parent disease group, or null if this is a top-level group (i.e. a cluster).
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private DiseaseGroup parentGroup;

    // The disease group name.
    @Column(nullable = false)
    private String name;

    // The disease group type.
    @Column(name = "group_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DiseaseGroupType groupType;

    // The disease group public name (for display).
    @Column(name = "public_name")
    private String publicName;

    // The disease group short name (for display).
    @Column(name = "short_name")
    private String shortName;

    // The disease group abbreviated name.
    private String abbreviation;

    // True if the disease group is global, false if tropical, null if unknown.
    @Column(name = "is_global")
    private Boolean isGlobal;

    // True if the disease group is an ABRAID priority disease.
    @Column(name = "is_priority_disease", nullable = false)
    private boolean isPriorityDisease = false;

    // A link to a further grouping of diseases for use by experts in the Data Validator.
    @ManyToOne
    @JoinColumn(name = "validator_disease_group_id")
    private ValidatorDiseaseGroup validatorDiseaseGroup;

    // The weighting, initially determined by group type.
    @Column
    private Double weighting;

    // The SDM model mode to use for this disease.
    @Column(name = "model_mode")
    private String modelMode;

    // The max number of days between model runs that are triggered by the Data Manager.
    @Column(name = "max_days_between_runs")
    private int maxDaysBetweenModelRuns;

    // The date on which the disease extent was last generated, whether a one-off action or preparation for a model run.
    @Column(name = "last_extent_generation_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastExtentGenerationDate;

    // The date on which the weightings were last updated, in preparation for a model run.
    @Column(name = "last_model_run_prep_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastModelRunPrepDate;

    // When the system was approved by an administrator to run the weekly model automatically, if yet.
    @Column(name = "automatic_model_runs_start_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime automaticModelRunsStartDate;

    // The minimum number of new distinct locations required to trigger a model run.
    @Column(name = "min_new_locations_trigger")
    private Integer minNewLocationsTrigger;

    @Column(name = "max_env_suitability")
    private Double maxEnvironmentalSuitabilityForTriggering;

    @Column(name = "min_distance_from_extent")
    private Double minDistanceFromDiseaseExtentForTriggering;

    // The minimum number of occurrences required for a model run to go ahead.
    // Default value is implicitly zero.
    @Column(name = "min_data_volume")
    private int minDataVolume;

    // The following parameters define the Minimum Data Spread conditions, which must be satisfied for a model run to
    // go ahead: There must be at least one occurrence in [minDistinctCountries] and more than [highFrequencyThreshold]
    // occurrences in [minHighFrequencyCountries].
    @Column(name = "min_distinct_countries")
    private Integer minDistinctCountries;

    @Column(name = "high_frequency_threshold")
    private Integer highFrequencyThreshold;

    @Column(name = "min_high_frequency_countries")
    private Integer minHighFrequencyCountries;

    // If true, only the subset of countries (determined by forMinDataSpread flag on a Country) should be considered.
    // Otherwise, all countries are considered.
    @Column(name = "occurs_in_africa")
    private Boolean occursInAfrica;

    @OneToOne(mappedBy = "diseaseGroup", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private DiseaseExtent diseaseExtentParameters;

    // True if machine learning should be used to determine whether or not to send a disease occurrence to the Data
    // Validator, otherwise false
    @Column(name = "use_machine_learning")
    private boolean useMachineLearning = true;

    // If useMachineLearning is false, occurrences whose environmental suitability is less than this value will be
    // sent to the Data Validator
    @Column(name = "max_env_suitability_without_ml")
    private Double maxEnvironmentalSuitabilityWithoutML;

    // The agent type of this disease.
    @Column(name = "agent_type")
    @Enumerated(EnumType.STRING)
    private DiseaseGroupAgentType agentType;

    // True if non-bespoke sample bias data sets for this disease should be filtered by agent type, otherwise false.
    @Column(name = "filter_bias_data_by_agent_type")
    private boolean filterBiasDataByAgentType = false;

    // The database row creation date.
    @Column(name = "created_date", insertable = false, updatable = false)
    @Generated(value = GenerationTime.INSERT)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdDate;

    public DiseaseGroup() {
    }

    public DiseaseGroup(String name) {
        this.name = name;
    }

    public DiseaseGroup(Integer id) {
        this.id = id;
    }

    public DiseaseGroup(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public DiseaseGroup(DiseaseGroup parentGroup, String name, DiseaseGroupType groupType) {
        this.parentGroup = parentGroup;
        this.name = name;
        this.groupType = groupType;
    }

    public DiseaseGroup(Integer id, DiseaseGroup parentGroup, String name, DiseaseGroupType groupType) {
        this.id = id;
        this.parentGroup = parentGroup;
        this.name = name;
        this.groupType = groupType;
    }

    public DiseaseGroup(String name, ValidatorDiseaseGroup validatorDiseaseGroup) {
        this.name = name;
        this.validatorDiseaseGroup = validatorDiseaseGroup;
    }

    public Integer getId() {
        return id;
    }

    public DiseaseGroup getParentGroup() {
        return parentGroup;
    }

    public void setParentGroup(DiseaseGroup parentGroup) {
        this.parentGroup = parentGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DiseaseGroupType getGroupType() {
        return groupType;
    }

    public void setGroupType(DiseaseGroupType groupType) {
        this.groupType = groupType;
    }

    public String getPublicName() {
        return publicName;
    }

    public void setPublicName(String publicName) {
        this.publicName = publicName;
    }

    /**
     * Gets the disease group's public name for display, if it exists.
     * @return The disease group's public name for display.
     */
    public String getPublicNameForDisplay() {
        return (StringUtils.hasText(getPublicName())) ? getPublicName() : getName();
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Gets the disease group's short name for display, if it exists.
     * This should only be used in dropdown menus, as the HTML formatting that may be present in the public name
     * cannot be displayed in dropdowns.
     * @return The disease group's short name for display.
     */
    public String getShortNameForDisplay() {
        return StringUtils.hasText(getShortName()) ? getShortName() : getName();
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public Boolean isGlobal() {
        return isGlobal;
    }

    public void setGlobal(Boolean isGlobal) {
        this.isGlobal = isGlobal;
    }

    public boolean isPriorityDisease() {
        return isPriorityDisease;
    }

    public void setPriorityDisease(boolean isPriorityDisease) {
        this.isPriorityDisease = isPriorityDisease;
    }

    public ValidatorDiseaseGroup getValidatorDiseaseGroup() {
        return validatorDiseaseGroup;
    }

    public void setValidatorDiseaseGroup(ValidatorDiseaseGroup validatorDiseaseGroup) {
        this.validatorDiseaseGroup = validatorDiseaseGroup;
    }

    public Double getWeighting() {
        return weighting;
    }

    public void setWeighting(Double weighting) {
        this.weighting = weighting;
    }

    public String getModelMode() {
        return modelMode;
    }

    public void setModelMode(String modelMode) {
        this.modelMode = modelMode;
    }

    public int getMaxDaysBetweenModelRuns() {
        return maxDaysBetweenModelRuns;
    }

    public void setMaxDaysBetweenModelRuns(int maxDaysBetweenModelRuns) {
        this.maxDaysBetweenModelRuns = maxDaysBetweenModelRuns;
    }

    public DateTime getLastExtentGenerationDate() {
        return lastExtentGenerationDate;
    }

    public void setLastExtentGenerationDate(DateTime lastExtentGenerationDate) {
        this.lastExtentGenerationDate = lastExtentGenerationDate;
    }

    public DateTime getLastModelRunPrepDate() {
        return lastModelRunPrepDate;
    }

    public void setLastModelRunPrepDate(DateTime lastModelRunPrepDate) {
        this.lastModelRunPrepDate = lastModelRunPrepDate;
    }

    public boolean isAutomaticModelRunsEnabled() {
        return (automaticModelRunsStartDate != null);
    }

    public DateTime getAutomaticModelRunsStartDate() {
        return automaticModelRunsStartDate;
    }

    public void setAutomaticModelRunsStartDate(DateTime automaticModelRunsStartDate) {
        this.automaticModelRunsStartDate = automaticModelRunsStartDate;
    }

    public Integer getMinNewLocationsTrigger() {
        return minNewLocationsTrigger;
    }

    public void setMinNewLocationsTrigger(Integer minNewLocations) {
        this.minNewLocationsTrigger = minNewLocations;
    }

    public Double getMaxEnvironmentalSuitabilityForTriggering() {
        return maxEnvironmentalSuitabilityForTriggering;
    }

    public void setMaxEnvironmentalSuitabilityForTriggering(Double maxEnvironmentalSuitabilityForTriggering) {
        this.maxEnvironmentalSuitabilityForTriggering = maxEnvironmentalSuitabilityForTriggering;
    }

    public Double getMinDistanceFromDiseaseExtentForTriggering() {
        return minDistanceFromDiseaseExtentForTriggering;
    }

    public void setMinDistanceFromDiseaseExtentForTriggering(Double minDistanceFromDiseaseExtent) {
        this.minDistanceFromDiseaseExtentForTriggering = minDistanceFromDiseaseExtent;
    }

    public int getMinDataVolume() {
        return minDataVolume;
    }

    public void setMinDataVolume(int minDataVolume) {
        this.minDataVolume = minDataVolume;
    }

    public Integer getMinDistinctCountries() {
        return minDistinctCountries;
    }

    public void setMinDistinctCountries(Integer minDistinctCountries) {
        this.minDistinctCountries = minDistinctCountries;
    }

    public Integer getHighFrequencyThreshold() {
        return highFrequencyThreshold;
    }

    public void setHighFrequencyThreshold(Integer highFrequencyThreshold) {
        this.highFrequencyThreshold = highFrequencyThreshold;
    }

    public Integer getMinHighFrequencyCountries() {
        return minHighFrequencyCountries;
    }

    public void setMinHighFrequencyCountries(Integer minHighFrequencyCountries) {
        this.minHighFrequencyCountries = minHighFrequencyCountries;
    }

    public DiseaseExtent getDiseaseExtentParameters() {
        return diseaseExtentParameters;
    }

    public void setDiseaseExtentParameters(DiseaseExtent diseaseExtentParameters) {
        this.diseaseExtentParameters = diseaseExtentParameters;
    }

    /**
     * Whether the disease group is known to occur in Africa.
     * @return True if the disease group occurs in Africa.
     */
    public Boolean occursInAfrica() {
        return occursInAfrica;
    }

    public void setOccursInAfrica(Boolean occursInAfrica) {
        this.occursInAfrica = occursInAfrica;
    }

    /**
     * Whether to use machine learning for sending occurrences to the Data Validator.
     * @return True if machine learning should be used for sending occurrences to the Data Validator.
     */
    public boolean useMachineLearning() {
        return useMachineLearning;
    }

    public void setUseMachineLearning(boolean useMachineLearning) {
        this.useMachineLearning = useMachineLearning;
    }

    public Double getMaxEnvironmentalSuitabilityWithoutML() {
        return maxEnvironmentalSuitabilityWithoutML;
    }

    public void setMaxEnvironmentalSuitabilityWithoutML(Double maxEnvironmentalSuitabilityWithoutML) {
        this.maxEnvironmentalSuitabilityWithoutML = maxEnvironmentalSuitabilityWithoutML;
    }

    /**
     * Should non-bespoke sample bias data sets for this disease should be filtered by agent type.
     * @return True if non-bespoke sample bias data sets for this disease should be filtered by agent type,
     *          otherwise false.
     */
    public boolean shouldFilterBiasDataByAgentType() {
        return filterBiasDataByAgentType;
    }

    public void setFilterBiasDataByAgentType(boolean filterBiasDataByAgentType) {
        this.filterBiasDataByAgentType = filterBiasDataByAgentType;
    }

    public DiseaseGroupAgentType getAgentType() {
        return agentType;
    }

    public void setAgentType(DiseaseGroupAgentType agentType) {
        this.agentType = agentType;
    }

    public DateTime getCreatedDate() {
        return createdDate;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiseaseGroup that = (DiseaseGroup) o;

        if (filterBiasDataByAgentType != that.filterBiasDataByAgentType) return false;
        if (isPriorityDisease != that.isPriorityDisease) return false;
        if (maxDaysBetweenModelRuns != that.maxDaysBetweenModelRuns) return false;
        if (minDataVolume != that.minDataVolume) return false;
        if (useMachineLearning != that.useMachineLearning) return false;
        if (abbreviation != null ? !abbreviation.equals(that.abbreviation) : that.abbreviation != null) return false;
        if (agentType != that.agentType) return false;
        if (automaticModelRunsStartDate != null ? !automaticModelRunsStartDate.equals(that.automaticModelRunsStartDate) : that.automaticModelRunsStartDate != null)
            return false;
        if (createdDate != null ? !createdDate.equals(that.createdDate) : that.createdDate != null) return false;
        if (diseaseExtentParameters != null ? !diseaseExtentParameters.equals(that.diseaseExtentParameters) : that.diseaseExtentParameters != null)
            return false;
        if (groupType != that.groupType) return false;
        if (highFrequencyThreshold != null ? !highFrequencyThreshold.equals(that.highFrequencyThreshold) : that.highFrequencyThreshold != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (isGlobal != null ? !isGlobal.equals(that.isGlobal) : that.isGlobal != null) return false;
        if (lastExtentGenerationDate != null ? !lastExtentGenerationDate.equals(that.lastExtentGenerationDate) : that.lastExtentGenerationDate != null)
            return false;
        if (lastModelRunPrepDate != null ? !lastModelRunPrepDate.equals(that.lastModelRunPrepDate) : that.lastModelRunPrepDate != null)
            return false;
        if (maxEnvironmentalSuitabilityForTriggering != null ? !maxEnvironmentalSuitabilityForTriggering.equals(that.maxEnvironmentalSuitabilityForTriggering) : that.maxEnvironmentalSuitabilityForTriggering != null)
            return false;
        if (maxEnvironmentalSuitabilityWithoutML != null ? !maxEnvironmentalSuitabilityWithoutML.equals(that.maxEnvironmentalSuitabilityWithoutML) : that.maxEnvironmentalSuitabilityWithoutML != null)
            return false;
        if (minDistanceFromDiseaseExtentForTriggering != null ? !minDistanceFromDiseaseExtentForTriggering.equals(that.minDistanceFromDiseaseExtentForTriggering) : that.minDistanceFromDiseaseExtentForTriggering != null)
            return false;
        if (minDistinctCountries != null ? !minDistinctCountries.equals(that.minDistinctCountries) : that.minDistinctCountries != null)
            return false;
        if (minHighFrequencyCountries != null ? !minHighFrequencyCountries.equals(that.minHighFrequencyCountries) : that.minHighFrequencyCountries != null)
            return false;
        if (minNewLocationsTrigger != null ? !minNewLocationsTrigger.equals(that.minNewLocationsTrigger) : that.minNewLocationsTrigger != null)
            return false;
        if (modelMode != null ? !modelMode.equals(that.modelMode) : that.modelMode != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (occursInAfrica != null ? !occursInAfrica.equals(that.occursInAfrica) : that.occursInAfrica != null)
            return false;
        if (parentGroup != null ? !parentGroup.equals(that.parentGroup) : that.parentGroup != null) return false;
        if (publicName != null ? !publicName.equals(that.publicName) : that.publicName != null) return false;
        if (shortName != null ? !shortName.equals(that.shortName) : that.shortName != null) return false;
        if (validatorDiseaseGroup != null ? !validatorDiseaseGroup.equals(that.validatorDiseaseGroup) : that.validatorDiseaseGroup != null)
            return false;
        if (weighting != null ? !weighting.equals(that.weighting) : that.weighting != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (parentGroup != null ? parentGroup.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (groupType != null ? groupType.hashCode() : 0);
        result = 31 * result + (publicName != null ? publicName.hashCode() : 0);
        result = 31 * result + (shortName != null ? shortName.hashCode() : 0);
        result = 31 * result + (abbreviation != null ? abbreviation.hashCode() : 0);
        result = 31 * result + (isGlobal != null ? isGlobal.hashCode() : 0);
        result = 31 * result + (isPriorityDisease ? 1 : 0);
        result = 31 * result + (validatorDiseaseGroup != null ? validatorDiseaseGroup.hashCode() : 0);
        result = 31 * result + (weighting != null ? weighting.hashCode() : 0);
        result = 31 * result + (modelMode != null ? modelMode.hashCode() : 0);
        result = 31 * result + maxDaysBetweenModelRuns;
        result = 31 * result + (lastExtentGenerationDate != null ? lastExtentGenerationDate.hashCode() : 0);
        result = 31 * result + (lastModelRunPrepDate != null ? lastModelRunPrepDate.hashCode() : 0);
        result = 31 * result + (automaticModelRunsStartDate != null ? automaticModelRunsStartDate.hashCode() : 0);
        result = 31 * result + (minNewLocationsTrigger != null ? minNewLocationsTrigger.hashCode() : 0);
        result = 31 * result + (maxEnvironmentalSuitabilityForTriggering != null ? maxEnvironmentalSuitabilityForTriggering.hashCode() : 0);
        result = 31 * result + (minDistanceFromDiseaseExtentForTriggering != null ? minDistanceFromDiseaseExtentForTriggering.hashCode() : 0);
        result = 31 * result + minDataVolume;
        result = 31 * result + (minDistinctCountries != null ? minDistinctCountries.hashCode() : 0);
        result = 31 * result + (highFrequencyThreshold != null ? highFrequencyThreshold.hashCode() : 0);
        result = 31 * result + (minHighFrequencyCountries != null ? minHighFrequencyCountries.hashCode() : 0);
        result = 31 * result + (occursInAfrica != null ? occursInAfrica.hashCode() : 0);
        result = 31 * result + (diseaseExtentParameters != null ? diseaseExtentParameters.hashCode() : 0);
        result = 31 * result + (useMachineLearning ? 1 : 0);
        result = 31 * result + (maxEnvironmentalSuitabilityWithoutML != null ? maxEnvironmentalSuitabilityWithoutML.hashCode() : 0);
        result = 31 * result + (agentType != null ? agentType.hashCode() : 0);
        result = 31 * result + (filterBiasDataByAgentType ? 1 : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
