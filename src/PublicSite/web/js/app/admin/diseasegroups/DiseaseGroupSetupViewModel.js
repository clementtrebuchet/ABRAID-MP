/* An AMD defining the view-model for the disease group setup (in administration).
 * Copyright (c) 2014 University of Oxford
 */
define([
    "ko",
    "jquery",
    "moment",
    "app/BaseFormViewModel"
], function (ko, $, moment, BaseFormViewModel) {
    "use strict";

    return function (baseUrl, diseaseGroupSelectedEventName, diseaseGroupSavedEventName) {
        var self = this;
        BaseFormViewModel.call(self, false, true, undefined, undefined, { success: "Model run requested." }, true);

        self.selectedDiseaseGroupId = ko.observable();
        self.hasModelBeenSuccessfullyRun = ko.observable(false);
        self.lastModelRunText = ko.observable("");
        self.diseaseOccurrencesText = ko.observable("");
        self.canRunModel = ko.observable(false).extend({equal: true});
        self.batchEndDate = ko.observable("").extend({required: true, date: true});
        self.batchEndDateMinimum = ko.observable("");
        self.batchEndDateMaximum = ko.observable("");

        self.buildSubmissionUrl = function () {
            return baseUrl + "admin/diseasegroups/" + self.selectedDiseaseGroupId() + "/requestmodelrun";
        };

        self.buildSubmissionData = function () {
            var batchEndDateText = moment(self.batchEndDate(), "DD MMM YYYY").format();
            return { batchEndDate: batchEndDateText };
        };

        self.resetState = function () { // only public for testing
            self.notices.removeAll();
            self.lastModelRunText("");
            self.diseaseOccurrencesText("");
            self.batchEndDate("");
            self.batchEndDateMinimum("");
            self.batchEndDateMaximum("");
            self.hasModelBeenSuccessfullyRun(false);
            self.canRunModel(false);
        };

        self.updateModelRunInfo = function (diseaseGroupId) { // only public for testing
            self.resetState();
            self.selectedDiseaseGroupId(diseaseGroupId);

            if (self.selectedDiseaseGroupId()) {
                self.isSubmitting(true);
                // Get information regarding model runs for this disease group
                var url = baseUrl + "admin/diseasegroups/" + self.selectedDiseaseGroupId() + "/modelruninformation";
                $.getJSON(url)
                    .done(function (data) {
                        self.lastModelRunText(data.lastModelRunText);
                        self.diseaseOccurrencesText(data.diseaseOccurrencesText);
                        self.batchEndDate(data.batchEndDateDefault);
                        self.batchEndDateMinimum(data.batchEndDateMinimum);
                        self.batchEndDateMaximum(data.batchEndDateMaximum);
                        self.hasModelBeenSuccessfullyRun(data.hasModelBeenSuccessfullyRun);
                        self.canRunModel(data.canRunModel);
                        if (!self.canRunModel()) {
                            var errorMessage = "Cannot run model because " + data.cannotRunModelReason;
                            self.notices.push({ message: errorMessage, priority: "warning"});
                        }
                    })
                    .fail(function () {
                        self.notices.push({ message: "Could not retrieve model run details.", priority: "warning"});
                    })
                    .always(function () { self.isSubmitting(false); });
            }
        };

        // Called when a disease group is selected from the drop-down list
        ko.postbox.subscribe(diseaseGroupSelectedEventName, function (selectedDiseaseGroup) {
            self.updateModelRunInfo(selectedDiseaseGroup.id);
        });

        // Called when changes to a disease group are successfully saved
        ko.postbox.subscribe(diseaseGroupSavedEventName, function (diseaseGroupId) {
            self.updateModelRunInfo(diseaseGroupId);
        });
    };
});