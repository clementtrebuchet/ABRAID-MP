/* An AMD defining the view-model for the main settings of the selected disease group.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "underscore"
], function (ko, _) {
    "use strict";

    var SINGLE = "SINGLE";
    var MICROCLUSTER = "MICROCLUSTER";
    var CLUSTER = "CLUSTER";
    var groupTypes = [
        {value: SINGLE,       label: "Single Disease"},
        {value: MICROCLUSTER, label: "Microcluster"},
        {value: CLUSTER,      label: "Cluster"}
    ];

    return function (diseaseGroups, validatorDiseaseGroups, diseaseGroupSelectedEventName) {
        var self = this;

        var findInList = function (diseaseGroup, diseaseGroupsList) {
            if (diseaseGroup) {
                return _(diseaseGroupsList).findWhere({ id: diseaseGroup.id });
            }
        };

        var filterOnType = function (diseaseGroups, groupType) {
            return _(diseaseGroups).filter(function (diseaseGroup) { return diseaseGroup.groupType === groupType; });
        };

        var microclusterDiseaseGroups = filterOnType(diseaseGroups, MICROCLUSTER);
        var clusterDiseaseGroups = filterOnType(diseaseGroups, CLUSTER);

        var getParentDiseaseGroups = function (type) {
            switch (type) {
                case SINGLE:
                    return microclusterDiseaseGroups;
                case MICROCLUSTER:
                    return clusterDiseaseGroups;
                case CLUSTER:
                    return null;
            }
        };

        self.name = ko.observable().extend({ required: true });
        self.publicName = ko.observable();
        self.shortName = ko.observable();
        self.abbreviation = ko.observable();
        self.groupTypes = groupTypes;
        self.selectedType = ko.observable();
        self.isGlobal = ko.observable();
        self.parentDiseaseGroups = ko.computed(function () { return getParentDiseaseGroups(self.selectedType()); });
        self.selectedParentDiseaseGroup = ko.observable();
        self.enableParentDiseaseGroups = ko.computed(function () { return self.selectedType() !== CLUSTER; });
        self.validatorDiseaseGroups = validatorDiseaseGroups;
        self.selectedValidatorDiseaseGroup = ko.observable();

        ko.postbox.subscribe(diseaseGroupSelectedEventName, function (diseaseGroup) {
            self.name(diseaseGroup.name);
            self.publicName(diseaseGroup.publicName);
            self.shortName(diseaseGroup.shortName);
            self.abbreviation(diseaseGroup.abbreviation);
            self.selectedType(diseaseGroup.groupType);
            self.isGlobal(diseaseGroup.isGlobal);
            var parentDiseaseGroup = findInList(diseaseGroup.parentDiseaseGroup, self.parentDiseaseGroups());
            self.selectedParentDiseaseGroup(parentDiseaseGroup);
            var validatorDiseaseGroup = findInList(diseaseGroup.validatorDiseaseGroup, self.validatorDiseaseGroups);
            self.selectedValidatorDiseaseGroup(validatorDiseaseGroup);
        });
    };
});
