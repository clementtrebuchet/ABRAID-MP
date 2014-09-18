package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.csv.domain;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.ParseUtils;

import java.io.IOException;
import java.util.List;

/**
 * Represents a disease occurrence as provided in a generic CSV file.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class CsvDiseaseOccurrence {
    private String site;
    private Double longitude;
    private Double latitude;
    private String precision;
    private String countryName;
    private String diseaseGroupName;
    private String occurrenceDate;
    private String title;
    private String summary;
    private String url;

    public CsvDiseaseOccurrence() {
    }

    public CsvDiseaseOccurrence(String site, Double longitude, Double latitude, String precision, String countryName,
                                String diseaseGroupName, String occurrenceDate, String title, String summary,
                                String url) {
        this.site = site;
        this.longitude = longitude;
        this.latitude = latitude;
        this.precision = precision;
        this.countryName = countryName;
        this.diseaseGroupName = diseaseGroupName;
        this.occurrenceDate = occurrenceDate;
        this.title = title;
        this.summary = summary;
        this.url = url;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = ParseUtils.convertString(site);
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getPrecision() {
        return precision;
    }

    public void setPrecision(String precision) {
        this.precision = ParseUtils.convertString(precision);
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = ParseUtils.convertString(countryName);
    }

    public String getDiseaseGroupName() {
        return diseaseGroupName;
    }

    public void setDiseaseGroupName(String diseaseGroupName) {
        this.diseaseGroupName = ParseUtils.convertString(diseaseGroupName);
    }

    public String getOccurrenceDate() {
        return occurrenceDate;
    }

    public void setOccurrenceDate(String occurrenceDate) {
        this.occurrenceDate = ParseUtils.convertString(occurrenceDate);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = ParseUtils.convertString(title);
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = ParseUtils.convertString(summary);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = ParseUtils.convertString(url);
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CsvDiseaseOccurrence that = (CsvDiseaseOccurrence) o;

        if (countryName != null ? !countryName.equals(that.countryName) : that.countryName != null) return false;
        if (diseaseGroupName != null ? !diseaseGroupName.equals(that.diseaseGroupName) : that.diseaseGroupName != null)
            return false;
        if (latitude != null ? !latitude.equals(that.latitude) : that.latitude != null) return false;
        if (longitude != null ? !longitude.equals(that.longitude) : that.longitude != null) return false;
        if (occurrenceDate != null ? !occurrenceDate.equals(that.occurrenceDate) : that.occurrenceDate != null)
            return false;
        if (precision != null ? !precision.equals(that.precision) : that.precision != null) return false;
        if (site != null ? !site.equals(that.site) : that.site != null) return false;
        if (summary != null ? !summary.equals(that.summary) : that.summary != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = site != null ? site.hashCode() : 0;
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + (precision != null ? precision.hashCode() : 0);
        result = 31 * result + (countryName != null ? countryName.hashCode() : 0);
        result = 31 * result + (diseaseGroupName != null ? diseaseGroupName.hashCode() : 0);
        result = 31 * result + (occurrenceDate != null ? occurrenceDate.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (summary != null ? summary.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON

    /**
     * Parses a collection of CsvDiseaseOccurrence entries from a csv string (header row required).
     * @param csv The csv string.
     * @return A collection of CsvDiseaseOccurrence entries.
     * @throws IOException Thrown if the parsing fails.
     */
    public static List<CsvDiseaseOccurrence> readFromCsv(String csv) throws IOException {
        CsvSchema schema = CsvSchema.builder()
                .setSkipFirstDataRow(true)
                .addColumn("site")
                .addColumn("longitude", CsvSchema.ColumnType.NUMBER)
                .addColumn("latitude", CsvSchema.ColumnType.NUMBER)
                .addColumn("precision")
                .addColumn("countryName")
                .addColumn("diseaseGroupName")
                .addColumn("occurrenceDate")
                .addColumn("title")
                .addColumn("summary")
                .addColumn("url")
                .build();

        return ParseUtils.readFromCsv(csv, CsvDiseaseOccurrence.class, schema);
    }
}
