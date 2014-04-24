-- Script: tables.sql
--
-- Description: Creates tables for the ABRAID-MP database.
--
-- Copyright (c) 2014 University of Oxford


-- List of tables:
--
-- admin_unit_global:              Represents an admin 0/1 area. As admin_unit_tropical, except ten large countries have been divided into admin 1 areas, to use for global diseases.
-- admin_unit_review:              Represents an expert's response to the presence or absence of a disease group across an admin unit.
-- admin_unit_qc:                  Represents an admin 1/2 area for the purposes of QC (Quality Control).
--                                 Imported from the standard SEEG/GAUL admin 1 and admin 2 shapefiles, with smaller islands removed.
-- admin_unit_simplified_global:   Represents an admin 0/1 area. As admin_unit_global, except with simplified borders to improve rendering performance.
-- admin_unit_simplified_tropical: Represents an admin 0/1 area. As admin_unit_tropical, except with simplified borders to improve rendering performance.
-- admin_unit_tropical:            Represents an admin 0/1 area. Tailored for ABRAID-MP by separating non-contiguous parts of countries, absorbing tiny countries, removing smaller
--                                 smaller islands etc. Eight large subtropical countries have been divided into admin 1 areas.
-- alert:                          Represents a report of a disease occurrence or occurrences, from a feed.
-- country:                        Represents a country as defined by SEEG. Imported from the standard SEEG/GAUL admin 0 shapefile, with smaller islands removed.
-- disease_group:                  Represents a group of diseases as defined by SEEG. This can be a disease cluster, disease microcluster, or a disease itself.
-- disease_occurrence:             Represents an occurrence of a disease group, in a location, as reported by an alert.
-- disease_occurrence_review:      Represents an expert's response on the validity of a disease occurrence point.
-- expert:                         Represents a user of the PublicSite.
-- expert_validator_disease_group: Represents an expert's disease interest, in terms of a disease group used by the Data Validator.
-- feed:                           Represents a source of alerts.
-- geoname:                        Represents a GeoName.
-- geonames_location_precision:    Represents a mapping between a GeoNames feature code and a location precision.
-- healthmap_country:              Represents a country as defined by HealthMap.
-- healthmap_country_country:      Represents a mapping between HealthMap countries and SEEG countries.
-- healthmap_disease:              Represents a disease as defined by HealthMap.
-- land_sea_border:                Represents a land-sea border to a 5km resolution as used by the model.
-- location:                       Represents the location of a disease occurrence.
-- provenance:                     Represents a provenance, i.e. the source of a group of feeds.
-- validator_disease_group:        Represents a grouping of diseases for use by the Data Validator.


CREATE TABLE admin_unit_qc (
    gaul_code integer NOT NULL,
    level varchar(1) NOT NULL,
    name varchar(100) NOT NULL,
    centr_lon double precision NOT NULL,
    centr_lat double precision NOT NULL,
    area double precision NOT NULL,
    geom geometry(MULTIPOLYGON, 4326)
);

CREATE TABLE admin_unit_disease_extent_class (
    id serial NOT NULL,
    global_gaul_code integer,
    tropical_gaul_code integer,
    disease_group_id integer NOT NULL,
    disease_extent_class varchar(17) NOT NULL,
    created_date timestamp NOT NULL DEFAULT LOCALTIMESTAMP
);

CREATE TABLE admin_unit_global (
    gaul_code integer NOT NULL,
    level varchar(1) NOT NULL,
    name varchar(100) NOT NULL,
    pub_name varchar(100) NOT NULL,
    geom geometry(MULTIPOLYGON, 4326)
);

CREATE TABLE admin_unit_review (
    id serial NOT NULL,
    expert_id integer NOT NULL,
    disease_group_id integer NOT NULL,
    global_gaul_code integer,
    tropical_gaul_code integer,
    response varchar(17) NOT NULL,
    created_date timestamp NOT NULL DEFAULT LOCALTIMESTAMP
);

CREATE TABLE admin_unit_simplified_global (
    gaul_code integer NOT NULL,
    name varchar(100) NOT NULL,
    pub_name varchar(100) NOT NULL,
    geom geometry(MULTIPOLYGON, 4326)
);

CREATE TABLE admin_unit_simplified_tropical (
    gaul_code integer NOT NULL,
    name varchar(100) NOT NULL,
    pub_name varchar(100) NOT NULL,
    geom geometry(MULTIPOLYGON, 4326)
);

CREATE TABLE admin_unit_tropical (
    gaul_code integer NOT NULL,
    level varchar(1) NOT NULL,
    name varchar(100) NOT NULL,
    pub_name varchar(100) NOT NULL,
    geom geometry(MULTIPOLYGON, 4326)
);

CREATE TABLE alert (
    id serial NOT NULL,
    feed_id integer NOT NULL,
    title text,
    publication_date timestamp NOT NULL,
    url varchar(2000) NOT NULL,
    summary text,
    healthmap_alert_id bigint NOT NULL,
    created_date timestamp NOT NULL DEFAULT LOCALTIMESTAMP
);

CREATE TABLE country (
    gaul_code integer NOT NULL,
    name varchar(100) NOT NULL,
    geom geometry(MULTIPOLYGON, 4326)
);

CREATE TABLE disease_group (
    id serial NOT NULL,
    parent_id integer,
    name varchar(100) NOT NULL,
    group_type varchar(15) NOT NULL,
    public_name varchar(100),
    short_name varchar(100),
    abbreviation varchar(10),
    is_global boolean,
    validator_disease_group_id integer,
    created_date timestamp NOT NULL DEFAULT LOCALTIMESTAMP
);

CREATE TABLE disease_occurrence (
    id serial NOT NULL,
    disease_group_id integer NOT NULL,
    location_id integer NOT NULL,
    alert_id integer NOT NULL,
    created_date timestamp NOT NULL DEFAULT LOCALTIMESTAMP,
    occurrence_date timestamp NOT NULL,
    validation_weighting double precision
);

CREATE TABLE disease_occurrence_review (
    id serial NOT NULL,
    expert_id integer NOT NULL,
    disease_occurrence_id integer NOT NULL,
    response varchar(6) NOT NULL,
    created_date timestamp NOT NULL DEFAULT LOCALTIMESTAMP
);

CREATE TABLE expert (
    id serial NOT NULL,
    name varchar(1000) NOT NULL,
    email varchar(320) NOT NULL,
    hashed_password varchar(60) NOT NULL,
    is_administrator boolean NOT NULL,
    weighting double precision,
    is_publicly_visible boolean,
    created_date timestamp NOT NULL DEFAULT LOCALTIMESTAMP
);

CREATE TABLE expert_validator_disease_group (
    expert_id integer NOT NULL,
    validator_disease_group_id integer NOT NULL,
    created_date timestamp NOT NULL DEFAULT LOCALTIMESTAMP
);

CREATE TABLE feed (
    id serial NOT NULL,
    provenance_id integer NOT NULL,
    name varchar(100) NOT NULL,
    weighting double precision NOT NULL,
    language varchar(4),
    healthmap_feed_id bigint,
    created_date timestamp NOT NULL DEFAULT LOCALTIMESTAMP
);

CREATE TABLE geoname (
    id integer NOT NULL,
    feature_code varchar(10) NOT NULL
);

CREATE TABLE geonames_location_precision (
    geonames_feature_code varchar(10) NOT NULL,
    location_precision varchar(10) NOT NULL
);

CREATE TABLE healthmap_country (
    id bigint NOT NULL,
    name varchar(100) NOT NULL
);

CREATE TABLE healthmap_country_country (
    healthmap_country_id bigint NOT NULL,
    gaul_code integer NOT NULL
);

CREATE TABLE healthmap_disease (
    id bigint NOT NULL,
    name varchar(100) NOT NULL,
    disease_group_id integer,
    created_date timestamp NOT NULL DEFAULT LOCALTIMESTAMP
);

CREATE TABLE land_sea_border (
    id integer NOT NULL,
    geom geometry(MULTIPOLYGON, 4326) NOT NULL
);

CREATE TABLE location (
    id serial NOT NULL,
    name varchar(1000),
    geom geometry(POINT, 4326) NOT NULL,
    precision varchar(10) NOT NULL,
    geoname_id integer,
    resolution_weighting double precision,
    created_date timestamp NOT NULL DEFAULT LOCALTIMESTAMP,
    healthmap_country_id bigint,
    admin_unit_qc_gaul_code integer,
    passed_qc_stage integer,
    qc_message varchar(1000)
);

CREATE TABLE provenance (
    id serial NOT NULL,
    name varchar(100) NOT NULL,
    default_feed_weighting double precision NOT NULL,
    created_date timestamp NOT NULL DEFAULT LOCALTIMESTAMP,
    last_retrieval_end_date timestamp
);

CREATE TABLE validator_disease_group (
    id serial NOT NULL,
    name varchar(100) NOT NULL,
    created_date timestamp NOT NULL DEFAULT LOCALTIMESTAMP
);
