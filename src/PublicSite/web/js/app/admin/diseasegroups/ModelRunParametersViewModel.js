/* An AMD defining the view-model for the model run parameters of the selected disease group.
 * Copyright (c) 2014 University of Oxford
 */
define(["ko"], function (ko) {
    "use strict";

    return function (diseaseGroupSelectedEventName) {
        var self = this;

        // Triggering a Model Run
        self.minNewLocations = ko.observable().extend({ digit: true, min: 0 });
        self.minEnvironmentalSuitability = ko.observable().extend({ number: true, min: 0, max: 1 });
        self.minDistanceFromDiseaseExtent = ko.observable().extend({ number: true });

        // Machine Learning
        self.useMachineLearning = ko.observable();
        self.maxEnvironmentalSuitabilityWithoutMLValue = ko.observable();
        self.maxEnvironmentalSuitabilityWithoutML = ko.computed({
            read: function () {
                return self.useMachineLearning() ? null : self.maxEnvironmentalSuitabilityWithoutMLValue();
            },
            write: function (value) { self.maxEnvironmentalSuitabilityWithoutMLValue(value); },
            owner: self
        }).extend({ number: true, min: 0, max: 1,
            required: ko.computed(function () { return !self.useMachineLearning(); })
        });

        // Minimum Data Volume and Minimum Data Spread
        self.minDataVolume = ko.observable().extend({ required: true, digit: true, min: 0 });
        self.minDistinctCountries = ko.observable().extend({ digit: true, min: 0 });
        self.occursInAfrica = ko.observable();

        self.minHighFrequencyCountriesValue = ko.observable();
        self.minHighFrequencyCountries = ko.computed({
            read:  function () { return self.occursInAfrica() ? self.minHighFrequencyCountriesValue() : null; },
            write: function (value) { self.minHighFrequencyCountriesValue(value); },
            owner: self
        }).extend({ digit: true, min: 0 });

        self.highFrequencyThresholdValue = ko.observable();
        self.highFrequencyThreshold = ko.computed({
            read:  function () { return self.occursInAfrica() ? self.highFrequencyThresholdValue() : null; },
            write: function (value) { self.highFrequencyThresholdValue(value); },
            owner: self
        }).extend({ digit: true, min: 0 });

        ko.postbox.subscribe(diseaseGroupSelectedEventName, function (diseaseGroup) {
            self.minNewLocations(ko.utils.normaliseInput(diseaseGroup.minNewLocations));
            self.minEnvironmentalSuitability(ko.utils.normaliseInput(diseaseGroup.minEnvironmentalSuitability));
            self.minDistanceFromDiseaseExtent(ko.utils.normaliseInput(diseaseGroup.minDistanceFromDiseaseExtent));
            self.minDataVolume(ko.utils.normaliseInput(diseaseGroup.minDataVolume));
            self.minDistinctCountries(ko.utils.normaliseInput(diseaseGroup.minDistinctCountries));
            self.minHighFrequencyCountries(ko.utils.normaliseInput(diseaseGroup.minHighFrequencyCountries));
            self.highFrequencyThreshold(ko.utils.normaliseInput(diseaseGroup.highFrequencyThreshold));
            self.occursInAfrica(diseaseGroup.occursInAfrica);
            self.useMachineLearning(diseaseGroup.useMachineLearning);
            self.maxEnvironmentalSuitabilityWithoutML(ko.utils.normaliseInput(
                diseaseGroup.maxEnvironmentalSuitabilityWithoutML));
        });
    };
});