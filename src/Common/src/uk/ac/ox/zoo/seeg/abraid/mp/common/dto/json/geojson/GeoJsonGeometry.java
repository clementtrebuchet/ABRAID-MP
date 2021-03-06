package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson;

import java.util.List;

/**
 * A DTO for "Geometry" objects.
 * Structured to reflect the fields that should be serialized in GeoJSON server response.
 * Implements the specification available from http://geojson.org/geojson-spec.html#geometry-objects
 * @param <TCrs> The type of crs.
 * @param <TCoordinates> The type of GeoJson Feature's coordinates.
 *
 * Copyright (c) 2014 University of Oxford
 */
public abstract class GeoJsonGeometry<TCrs extends GeoJsonCrs, TCoordinates> extends GeoJsonObject<TCrs> {
    private TCoordinates coordinates;

    public GeoJsonGeometry() {
    }

    public GeoJsonGeometry(GeoJsonGeometryType type, TCoordinates coordinates, TCrs crs, List<Double> bbox) {
        super(type.getGeoJsonObjectType(), crs, bbox);

        setCoordinates(coordinates);
    }

    public TCoordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(TCoordinates coordinates) {
        this.coordinates = coordinates;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GeoJsonGeometry that = (GeoJsonGeometry) o;

        if (coordinates != null ? !coordinates.equals(that.coordinates) : that.coordinates != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (coordinates != null ? coordinates.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
