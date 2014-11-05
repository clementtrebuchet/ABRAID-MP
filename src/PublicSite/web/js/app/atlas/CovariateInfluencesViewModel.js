/* AMD defining the predictors table on the atlas view.
 * Copyright (c) 2014 University of Oxford
 * - Events subscribed to:
 * -- 'selected-run' - published by LayerSelectorViewModel
 */
define([
    "ko",
    "jquery"
], function (ko, $) {
    "use strict";

    return function (baseUrl) {
        var self = this;

        self.activeRun =  ko.observable();
        self.covariateInfluences = ko.observable([]);
        self.effectCurvesLink = ko.computed(function () {
            return self.activeRun() ?
                baseUrl + "atlas/details/modelrun/" + self.activeRun() + "/effectcurves.csv" :
                "#";
        }, self);

        var ajax;

        ko.postbox.subscribe("selected-run", function (run) {
            self.covariateInfluences([]);

            if (run.id) {
                self.activeRun(run.id);
                if (ajax) {
                    ajax.abort();
                }
                ajax = $.getJSON(baseUrl + "atlas/details/modelrun/" + run.id + "/covariates")
                    .done(function (data) {
                        self.covariateInfluences(data);
                    });
            }
        });
    };
});