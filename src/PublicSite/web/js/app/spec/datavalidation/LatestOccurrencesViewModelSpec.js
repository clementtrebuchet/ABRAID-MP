/* A suite of tests for the LatestOccurrencesViewModel AMD.
 * Copyright (c) 2014 University of Oxford
 */
define([
    "app/datavalidation/LatestOccurrencesViewModel",
    "ko"
], function (LatestOccurrencesViewModel, ko) {
    "use strict";

    describe("The 'latest occurrences' view model", function () {

        describe("holds the count which", function () {
            var vm = new LatestOccurrencesViewModel();
            it("is an observable", function () {
                expect(vm.count).toBeObservable();
            });

            it("takes the expected initial value", function () {
                expect(vm.count()).toBe(0);
            });

            it("sets its value when the 'admin-unit-selected' event is fired", function () {
                // Arrange
                var expectedCount = 3;
                var data = { count: expectedCount };
                // Act
                ko.postbox.publish("admin-unit-selected", data);
                // Assert
                expect(vm.count()).toBe(expectedCount);
            });

            it("sets its value to 0 when the 'admin-unit-selected' event is fired with undefined", function () {
                // Arrange
                vm.count(6);
                // Act
                ko.postbox.publish("admin-unit-selected", undefined);
                // Assert
                expect(vm.count()).toBe(0);
            });

            it("sets its value to 0 when the 'admin-unit-selected' event is fired with empty", function () {
                // Arrange
                vm.count(6);
                var data = { count: 0 };
                // Act
                ko.postbox.publish("admin-unit-selected", data);
                // Assert
                expect(vm.count()).toBe(0);
            });

            it("sets its value to 0 when the 'layers-changed' event is fired", function () {
                // Arrange
                vm.count(6);
                // Act
                ko.postbox.publish("layers-changed", {});
                // Assert
                expect(vm.count()).toBe(0);
            });
        });

        describe("holds the list of occurrences which", function () {
            it("is an observable", function () {
                var vm = new LatestOccurrencesViewModel();
                expect(vm.occurrences).toBeObservable();
            });

            it("takes the expected initial value", function () {
                var vm = new LatestOccurrencesViewModel();
                expect(vm.occurrences()).toEqual([]);
            });

            describe("sets its value via an ajax call to expected url when the 'admin-unit-selected' event is fired",
                function () {
                    var baseUrl = "";
                    var adminUnit = { id: 123, count: 3 };

                    // JSON format is a feature collection, with up to 5 occurrences.
                    var json = { features: [
                        { properties: { id: 1, occurrenceDate: "01-10-2014" } },
                        { properties: { id: 2, occurrenceDate: "03-11-2014" } },
                        { properties: { id: 3, occurrenceDate: "05-12-2014" } }
                    ]};
                    // The properties are extracted from each feature, and the list sorted by occurrence date.
                    var expectedOccurrences = [
                        { id: 3, occurrenceDate: "05-12-2014" },
                        { id: 2, occurrenceDate: "03-11-2014" },
                        { id: 1, occurrenceDate: "01-10-2014" }
                    ];

                    it("when the user is logged in", function () {
                        // Arrange
                        var vm = new LatestOccurrencesViewModel(baseUrl, true);
                        var layer = { type: "disease extent", diseaseId: 87 };
                        var url = baseUrl + "datavalidation/diseases/87/adminunits/" + adminUnit.id + "/occurrences";

                        // Act
                        ko.postbox.publish("layers-changed", layer);
                        ko.postbox.publish("admin-unit-selected", adminUnit);
                        jasmine.Ajax.requests.mostRecent().response({
                            "status": 200,
                            "contentType": "application/json",
                            "responseText": JSON.stringify(json)
                        });

                        // Assert
                        expect(jasmine.Ajax.requests.mostRecent().url).toBe(url);
                        expect(vm.occurrences().length).toBe(3);
                        expect(vm.occurrences()).toEqual(expectedOccurrences);
                    });

                    it("when the user is not logged in", function () {
                        // Arrange
                        var vm = new LatestOccurrencesViewModel(baseUrl, false);    // jshint ignore:line
                        var url = baseUrl + "datavalidation/defaultadminunits/" + adminUnit.id + "/occurrences";

                        // Act
                        ko.postbox.publish("admin-unit-selected", adminUnit);
                        jasmine.Ajax.requests.mostRecent().response({
                            "status": 200,
                            "contentType": "application/json",
                            "responseText": JSON.stringify(json)
                        });

                        // Assert
                        expect(jasmine.Ajax.requests.mostRecent().url).toBe(url);
                    });

                    it("to empty when the event is fired with undefined", function () {
                        // Arrange
                        var vm = new LatestOccurrencesViewModel(baseUrl, true);
                        vm.occurrences([1, 2, 3]);
                        // Act
                        ko.postbox.publish("admin-unit-selected", undefined);
                        // Assert
                        expect(vm.occurrences().length).toBe(0);
                    });

                    it("to empty when the 'admin-unit-selected' event is fired with empty", function () {
                        // Arrange
                        var vm = new LatestOccurrencesViewModel(baseUrl, true);
                        vm.occurrences([1, 2, 3]);
                        var data = { id: 1, count: 0 };
                        // Act
                        ko.postbox.publish("admin-unit-selected", data);
                        // Assert
                        expect(vm.occurrences().length).toBe(0);
                    });
                }
            );

            it("sets its value to empty when the 'layers-changed' event is fired", function () {
                // Arrange
                var vm = new LatestOccurrencesViewModel();
                vm.occurrences([1, 2, 3]);
                // Act
                ko.postbox.publish("layers-changed", {});
                // Assert
                expect(vm.occurrences().length).toBe(0);
            });
        });

        describe("holds a boolean to indicate whether to show the list of occurrences which", function () {
            var vm = new LatestOccurrencesViewModel();
            it("is an observable", function () {
                expect(vm.showOccurrences).toBeObservable();
            });

            it("defaults to true", function () {
                expect(vm.showOccurrences()).toBe(true);
            });

            it("swaps its value to false via the 'toggle' function", function () {
                // Arrange
                vm.showOccurrences(true);
                // Act
                vm.toggle();
                // Assert
                expect(vm.showOccurrences()).toBe(false);
            });

            it("swaps its value to true via the 'toggle' function", function () {
                // Arrange
                vm.showOccurrences(false);
                // Act
                vm.toggle();
                // Assert
                expect(vm.showOccurrences()).toBe(true);
            });
        });
    });
});
