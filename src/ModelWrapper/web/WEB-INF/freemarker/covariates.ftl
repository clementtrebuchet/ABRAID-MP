<#--
    ModelWrapper's index landing page, to display model outputs.
    Copyright (c) 2014 University of Oxford
-->
<#import "common.ftl" as c/>
<#assign bootstrapData>
    <script type="text/javascript">
        // bootstrapped data for js viewmodels
        var initialData = ${initialData};
    </script>
</#assign>

<#assign templates>
    <script type="text/html" id="no-files-template">
        <tr class="warning">
            <td colspan="4" class="text-muted">No matching files.</td>
        </tr>
    </script>
    <script type="text/html" id="file-list-delete-template">
        <p>This file is currently used in <span data-bind="text: jsonFile.enabled.length"></span> <span data-bind="text: jsonFile.enabled.length === 1 ? 'disease' : 'diseases'"></span>. Are you sure you want to delete it?</p><br>
        <p style="text-align:center;">
            <span class="btn btn-default" data-bind="click: function () { hide(true) }" data-dismiss="popover">Confirm<span>
        </p>
    </script>
    <script type="text/html" id="file-list-template">
        <!-- ko foreach: visibleFiles -->
        <tr>
            <td data-bind="event: { mouseover: function() { mouseOver(true) }, mouseout: function() { mouseOver(false) } }">
                <input type="text" data-bind="value: name, attr: { title: name }, css: { 'transparent-input': !mouseOver() }" placeholder="No name given" >
            </td>
            <td><input type="text" data-bind="value: path, attr: { title: path }" readonly="true" class="transparent-input" ></td>
            <td><input type="checkbox" data-bind="checked: state"></td>
            <td>
                <button class="btn btn-default fa fa-lg fa-trash-o" data-bind="popover: { title: 'Delete file?', trigger: 'focus', placement: 'bottom', template: 'file-list-delete-template'}, click: function(data, event) { event.preventDefault(); }"></button>
                <span data-bind="if: info">
                    <i class="fa fa-lg fa-info-circle text-info" data-bind="tooltip: { title: info, placement: 'bottom' }"></i>&nbsp;
                </span>
            </td>
        </tr>
        <!-- /ko -->
    </script>
    <script type="text/html" id="file-list-header-template">
        <th data-bind="click: function () { $parent.updateSort(field.name) }">
            <span data-bind="text: display"></span>
            <span data-bind="text: ($parent.sortField() === field.name) ? ($parent.reverseSort() ? '&#9650;' : '&#9660;') : '&nbsp;'" class="up-down"></span>
        </th>
    </script>
</#assign>

<@c.page title="ABRAID-MP ModelWrapper" mainjs="/js/covariates" bootstrapData=bootstrapData templates=templates>
<div class="container">
    <div class="panel panel-default">
        <div class="panel-heading">
            <h2 class="panel-title">
                <a data-toggle="collapse" href="#add-covariate-body">
                    Add Covariate File
                </a>
            </h2>
        </div>
        <div class="panel-collapse collapse in" id="add-covariate-body">
            <div class="panel-body">
                <p>Use the fields below to add new covariate files to the system.</p>
                <form action="#">
                    <p class="form-group">
                        <label for="file-name">Name: </label>
                        <span class="input-group">
                            <span class="input-group-addon">
                                <i class="glyphicon glyphicon-pencil"></i>
                            </span>
                            <input id="file-name" type="text" class="form-control" placeholder="No name given" autocomplete="off">
                        </span>
                    </p>
                    <p class="form-group">
                        <label for="file-dir">Subdirectory: </label>
                        <span class="input-group">
                                <span class="input-group-addon">
                                <i class="glyphicon glyphicon-folder-open"></i>
                            </span>
                            <input id="file-dir" type="text" class="form-control" placeholder="./" autocomplete="off">
                        </span>
                    </p>
                    <p class="form-group">
                        <label for="file-picker">File: </label>
                        <span class="fileinput fileinput-new input-group" data-provides="fileinput">
                            <span class="input-group-addon">
                                <i class="glyphicon glyphicon-file"></i>
                            </span>
                            <span class="form-control" data-trigger="fileinput">
                                <span class="fileinput-filename"></span>
                            </span>
                            <span class="input-group-addon btn btn-default btn-file">
                                <span>Select file</span>
                                <input type="file" id="file-picker" placeholder="Choose a file">
                            </span>
                        </span>
                    </p>
                    <p class="form-group">
                        <a class="btn btn-primary" onclick="alert('Not implemented!')">Upload</a>
                    </p>
                    <div class="form-group" >
                        <div></div>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h2 class="panel-title">
                <a data-toggle="collapse" href="#covariate-body">
                    Covariate Settings
                </a>
            </h2>
        </div>
        <div class="panel-collapse collapse in" id="covariate-body">
            <div class="panel-body">
                <p>Use the fields below to update the existing covariate settings.</p>
                <form action="#">
                    <p class="form-group">
                        <label for="disease-picker">Disease: </label>
                        <span class="input-group">
                            <span class="input-group-addon">
                                <i class="fa fa-medkit"></i>
                            </span>
                            <select id="disease-picker" class="form-control" data-bind="options: diseases, value: selectedDisease, optionsText: 'name', disable: saving" ></select>
                        </span>
                    </p>
                    <p class="form-group">
                        <label for="file-filter">Filter: </label>
                        <span class="input-group">
                            <span class="input-group-addon">
                                <i class="glyphicon glyphicon-filter"></i>
                            </span>
                            <span id="filter-clear" class="clear glyphicon glyphicon-remove-circle" data-bind='click: function() { filter(""); }'></span>
                            <input id="file-filter" type="text" class="form-control" placeholder="Filter" autocomplete="off" data-bind="value: filter, valueUpdate:'afterkeydown', disable: saving">
                        </span>
                    </p>
                    <div>
                        <label for="file-list">Files: </label>
                        <div class="table-responsive">
                            <table id="file-list" class="table table-condensed table-hover">
                                <thead>
                                <tr data-bind="template: { name: 'file-list-header-template', foreach: [
                                    { name: 'name', display: 'Name' },
                                    { name: 'path', display: 'Path' },
                                    { name: 'state', display: 'Use for current disease' },
                                    { name: 'info', display: '+' }
                                ], as: 'field' }"></tr>
                                </thead>
                                <tbody data-bind="template: { name: visibleFiles().length == 0 ? 'no-files-template' : 'file-list-template' }"></tbody>
                            </table>
                        </div>
                    </div>
                    <p class="form-group">
                        <a class="btn btn-primary" data-bind="css: { 'disabled': saving }, click: submit">Save</a>
                    </p>
                    <div class="form-group" data-bind="foreach: notices">
                        <div data-bind="alert: $data"></div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
</@c.page>