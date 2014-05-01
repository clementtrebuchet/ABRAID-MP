/**
 * JS file for adding Leaflet map and layers.
 * Copyright (c) 2014 University of Oxford
 * Events subscribed to:
 * -- 'validation-type-changed' - to swap between DiseaseOccurrenceLayer and DiseaseExtentLayer
 * -- 'disease-set-changed'     - to swap the data on DiseaseOccurrenceLayer
 * -- 'disease-changed'         - to swap the data on DiseaseExtentLayer
 * -- 'point-reviewed'          - to remove the point from the map
 * Events published:
 * -- 'no-features-to-review'   - when the FeatureCollection data is empty, or the last point is remove from its layer
 * -- 'point-selected'
 * -- 'admin-unit-selected'
 */
define([
    "L",
    "jquery",
    "ko",
    "underscore"
], function (L, $, ko, _) {
    "use strict";

    return function (baseUrl, wmsUrl, loggedIn) {
        // Initialise map at "map" div
        var map = L.map("map", {
            attributionControl: false,
            zoomControl: false,
            zoomsliderControl: true,
            maxBounds: [ [-89, -179], [89, 179] ],
            maxZoom: 7,
            minZoom: 3,
            animate: true
        }).fitWorld();

        // Add the simplified shapefile base layer with WMS GET request
        L.tileLayer.wms(wmsUrl, {
            layers: ["abraid:simplified_base_layer"],
            format: "image/png",
            reuseTiles: true
        }).addTo(map);

        // Global colour variables
        var defaultColour = "#c478a9";      // Lighter pink/red
        var highlightColour = "#9e1e71";    // The chosen pink/red compatible with colourblindness
        var strokeColour = "#ce8eb8";       // Darker pink/red

        // Define default style for unselected points
        var diseaseOccurrenceLayerStyle = {
            stroke: false,
            fill: true,
            color: strokeColour,
            fillColor: defaultColour,
            fillOpacity: 0.8,
            radius: 8
        };

        // Change a point's colour on roll-over
        function highlightPoint(e) {
            e.target.setStyle({
                stroke: true,
                color: highlightColour
            });
        }

        // Return to default colour
        function resetHighlightedPoint(e) {
            e.target.setStyle({
                stroke: false,
                color: defaultColour
            });
        }

        // Change the point's colour and size when clicked
        function selectPoint(marker) {
            // First, reset the style of all other points on layer, so only one point is animated as selected at a time
            resetDiseaseOccurrenceLayerStyle();
            if (loggedIn) {
                marker.setStyle({
                    stroke: false,
                    fillColor: highlightColour,
                    radius: 13
                });
            } else {
                marker._icon.classList.add("marker-question-selected"); // jshint ignore:line
            }
        }

        // Define a circle, instead of the default leaflet marker, with listeners for mouse events
        function diseaseOccurrenceLayerPoint(feature, latlng) {
            var marker;
            if (loggedIn) {
                marker = L.circleMarker(latlng).on({
                    mouseover: highlightPoint,
                    mouseout: resetHighlightedPoint
                });
            } else {
                // Display a question mark marker
                marker = L.marker(latlng, { icon:
                    L.divIcon({
                        html: "<div><span>?</span></div>",
                        className: "marker-question",
                        iconAnchor: [10, 10]
                    })
                });
            }
            marker.on("click", function () {
                ko.postbox.publish("point-selected", feature);
                selectPoint(this);
            });
            return marker;
        }

        // Define a cluster icon, so that nearby occurrences are grouped into one marker.
        // Text displays the number of points in the cluster. Styling in separate CSS file.
        function clusterLayerPoint(cluster) {
            return new L.DivIcon({
                html: "<div><span>" + cluster.getChildCount() + "</span></div>",
                className: "marker-cluster",
                iconSize: new L.Point(40, 40)
            });
        }

        // Map from the id of a feature to its marker layer, so the layer object can be used in removeReviewedPoint.
        // Also serves to keep track of the number of markers on the diseaseOccurrenceLayer (for the selected disease).
        var layerMap = {};
        // Define styling of the layer to which occurrence data (feature collection) is later added via AJAX request
        var diseaseOccurrenceLayer = L.geoJson([], {
            pointToLayer: diseaseOccurrenceLayerPoint,
            style: diseaseOccurrenceLayerStyle,
            onEachFeature: function (feature, layer) {
                layer.on("add", function () {
                    layerMap[feature.id] = layer;
                });
            }
        });

        // Add this geoJson layer to the styled markerClusterGroup layer, so that nearby occurrences are grouped
        var clusterLayer = L.markerClusterGroup({
            maxClusterRadius: 10,
            polygonOptions: {
                color: highlightColour,
                weight: 2,
                fillColor: defaultColour,
                dashArray: 3
            },
            spiderfyDistanceMultiplier: 1.5,
            iconCreateFunction: clusterLayerPoint
        });

        // Reset the style of all markers on diseaseOccurrenceLayer
        function resetDiseaseOccurrenceLayerStyle() {
            if (loggedIn) {
                diseaseOccurrenceLayer.setStyle(diseaseOccurrenceLayerStyle);
            } else {
                Object.keys(layerMap).forEach(function (key) {
                    layerMap[key]._icon.classList.remove("marker-question-selected"); // jshint ignore:line
                });
            }
        }

        // Return the corresponding colour for the disease extent class of the admin unit
        var diseaseExtentClassColourScale = {
            "Presence":          "#8e1b65",  // dark pink
            "Possible presence": "#c478a9",  // light pink
            "Uncertain":         "#ffffbf",  // yellow
            "Possible absence":  "#b5caaa",  // light green
            "Absence":           "#769766"   // dark green
        };

        function diseaseExtentLayerStyle(feature) {
            return {
                fillColor: diseaseExtentClassColourScale[feature.properties.diseaseExtentClass],
                fillOpacity: 0.7,
                opacity: 0.7,
                weight: 2,
                color: "#ffffff" // white border
            };
        }

        function resetDiseaseExtentLayerStyle() {
            diseaseExtentLayer.setStyle({
                weight: 2,
                color: "#ffffff"
            });
        }

        function selectAdminUnit(layer) {
            resetDiseaseExtentLayerStyle();
            layer.setStyle({
                weight: 2.5,
                color: "#333333"
            });
            if (!L.Browser.ie && !L.Browser.opera) {
                layer.bringToFront();
            }
        }

        // Define the geoJson layer, to which the disease extent data will be added via AJAX request
        var diseaseExtentLayer = L.geoJson([], {
            style: diseaseExtentLayerStyle,
            onEachFeature: function (feature, layer) {
                layer.on({
                    click: function () {
                        selectAdminUnit(layer);
                        ko.postbox.publish("admin-unit-selected", feature);
                    },
                    mouseover: function () { layer.setStyle({ fillOpacity: 1 }); },
                    mouseout: function () { layer.setStyle({ fillOpacity: 0.7 }); }
                });
            }
        });

        function createLegendRow(className, colour) {
            var colourBox = "<i style='background:" + colour + "'></i>";
            var displayName = "<span>" + className + "</span><br>";
            return colourBox + displayName;
        }

        // Add a legend to display the disease extent class colour scale with corresponding class names
        var legend = L.control({position: "bottomleft"});
        legend.onAdd = function () {
            var div = L.DomUtil.create("div", "legend");
            var classNames = _.keys(diseaseExtentClassColourScale);
            var colours = _.values(diseaseExtentClassColourScale);
            for (var i = 0; i < classNames.length; i++) {
                div.innerHTML += createLegendRow(classNames[i], colours[i]);
            }
            return div;
        };

        var validationTypeIsDiseaseOccurrenceLayer;

        // Display the layer corresponding to the selected validation type (disease occurrences, or disease extent)
        function switchValidationTypeView() {
            if (validationTypeIsDiseaseOccurrenceLayer) {
                if (map.hasLayer(diseaseExtentLayer)) { map.removeControl(legend); }
                map.removeLayer(diseaseExtentLayer);
                clusterLayer.addLayer(diseaseOccurrenceLayer).addTo(map);
            } else {
                map.removeLayer(clusterLayer);
                diseaseExtentLayer.addTo(map);
                legend.addTo(map);
            }
        }

        // Remove the layers from the map, and clear the record of markers on the layer.
        // The new markers are added to layerMap on addLayer thanks to onEachFeature.
        function clearDiseaseOccurrenceLayer() {
            clusterLayer.clearLayers();
            diseaseOccurrenceLayer.clearLayers();
            layerMap = {};
        }

        function getGeoJsonRequestUrl(diseaseId) {
            var url = baseUrl;
            if (loggedIn) {
                url += "datavalidation/diseases/" + diseaseId +
                    (validationTypeIsDiseaseOccurrenceLayer ? "/occurrences" : "/adminunits");
            } else {
                url += "static/default" +
                    (validationTypeIsDiseaseOccurrenceLayer ? "Occurrences.json" : "AdminUnits.json");
            }
            return url;
        }

        // Add the new feature collection to the clustered layer, and zoom to its bounds
        function switchDiseaseOccurrenceLayer(diseaseId) {
            clearDiseaseOccurrenceLayer();
            $.getJSON(getGeoJsonRequestUrl(diseaseId), function (featureCollection) {
                if (featureCollection.features.length !== 0) {
                    ko.postbox.publish("no-features-to-review", false);
                    clusterLayer.addLayer(diseaseOccurrenceLayer.addData(featureCollection));
                    map.fitBounds(diseaseOccurrenceLayer.getBounds());
                } else {
                    ko.postbox.publish("no-features-to-review", true);
                    map.fitWorld();
                }
            });
        }

        // Display the admin units, and disease extent class, for the selected validator disease group.
        function switchDiseaseExtentLayer(diseaseId) {
            diseaseExtentLayer.clearLayers();
            $.getJSON(getGeoJsonRequestUrl(diseaseId), function (featureCollection) {
                if (featureCollection.features.length !== 0) {
                    ko.postbox.publish("no-features-to-review", false);
                    diseaseExtentLayer.addData(featureCollection);
                    //TODO: Fit bounds to the polygons with PRESENCE and POSSIBLE_PRESENCE class, and their neighbours
                } else {
                    ko.postbox.publish("no-features-to-review", true);
                }
            });
        }

        // Remove the feature's marker layer from the disease occurrence layer, and delete record of the feature.
        function removeMarkerFromMap(id) {
            clusterLayer.clearLayers();
            diseaseOccurrenceLayer.removeLayer(layerMap[id]);
            delete layerMap[id];
            if (_(layerMap).isEmpty()) {
                ko.postbox.publish("no-features-to-review", true);
            } else {
                clusterLayer.addLayer(diseaseOccurrenceLayer);
            }
        }

        // Reset to default style when a point or admin unit is unselected (by clicking anywhere else on the map)
        function resetSelectedFeature() {
            ko.postbox.publish("point-selected", null);
            resetDiseaseOccurrenceLayerStyle();

            ko.postbox.publish("admin-unit-selected", null);
            resetDiseaseExtentLayerStyle();
        }
        clusterLayer.on("clusterclick", resetSelectedFeature);
        map.on("click", resetSelectedFeature);

        ko.postbox.subscribe("validation-type-changed", function (type) {
            validationTypeIsDiseaseOccurrenceLayer = (type === "disease occurrences");
            resetSelectedFeature();
            switchValidationTypeView();
        });

        ko.postbox.subscribe("disease-set-changed", function (diseaseSet) {
            resetSelectedFeature();
            switchDiseaseOccurrenceLayer(diseaseSet.id);
        });

        ko.postbox.subscribe("disease-changed", function (disease) {
            resetSelectedFeature();
            switchDiseaseExtentLayer(disease.id);
        });

        ko.postbox.subscribe("point-reviewed", function (id) {
            removeMarkerFromMap(id);
        });
    };
});
