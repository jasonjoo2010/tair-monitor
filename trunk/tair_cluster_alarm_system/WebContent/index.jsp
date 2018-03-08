<%@ page language="java" contentType="text/html; charset=GB18030"
    pageEncoding="GB18030"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html> 
<head> 
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"> 
    <title>Form with embedded Grid</title> 
    <link rel="stylesheet" type="text/css" href="./resources/css/ext-all.css"/> 
 
    <!-- GC --> 
    <!-- LIBS --> 
    <script type="text/javascript" src="./adapter/ext/ext-base.js"></script> 
    <!-- ENDLIBS --> 
 
    <script type="text/javascript" src="./ext-all.js"></script> 
 
    <script type="text/javascript">
    Ext.onReady(function(){

        Ext.QuickTips.init();

        // turn on validation errors beside the field globally
        Ext.form.Field.prototype.msgTarget = 'side';

   		//   Define the Grid data and create the Grid
        store = new Ext.data.JsonStore({
				// store configs
				autoDestroy : true,
				url : 'Groupinfo2json',
				remoteSort : false,
				sortInfo : {
					field : 'Version',
					direction : 'ASC'
				},
				storeId : 'myStore',
				// reader configs
				root : 'root',
				totalProperty : 'totalproperty',
				fields : [
					{name: 'Port', mapping: 'Port', type: 'float'},
					{name: 'GroupName', mapping: 'GroupName'},
					{name: 'Domain', mapping: 'Domain'},
					{name: 'Application', mapping: 'Application'},
	                {name: 'IP', mapping: 'IP'},
	                {name: 'Scene', mapping: 'Scene'},
	                {name: 'Version', mapping: 'Version'}]
			});
        

         // the DefaultColumnModel expects this blob to define columns. It can be extended to provide
        // custom or reusable ColumnModels
        var colModel = new Ext.grid.ColumnModel([
            {header: "Domain", width: 200, sortable: true, locked:true, dataIndex: 'Domain'},
            {header: "IP", width: 100, sortable: true, dataIndex: 'IP'},
            {header: "Port", width: 55, sortable: true, dataIndex: 'Port'},
            {header: "GroupName", width: 120, sortable: true, dataIndex: 'GroupName'},
            {id:'Application',header: "Application", width: 80, sortable: true, dataIndex: 'Application'},
            {header: "Version", width: 60, sortable: true, dataIndex: 'Version'},
            {header: "Scene", width: 60, sortable: true, dataIndex: 'Scene'}
        ]);

    /*
     *    Here is where we create the Form
     */
        var gridForm = new Ext.FormPanel({
            id: 'group-form',
            frame: true,
            labelAlign: 'left',
            title: 'Group data',
            bodyStyle:'padding:5px',
            width: 1200,
            layout: 'column',    // Specifies that the items will now be arranged in columns
            items: [{
                columnWidth: 0.7,
                layout: 'fit',
                items: {
                    xtype: 'grid',
                    ds: store,
                    cm: colModel,
                    sm: new Ext.grid.RowSelectionModel({
                         singleSelect: true,
                        listeners: {
                            rowselect: function(sm, row, rec) {
                                Ext.getCmp("group-form").getForm().loadRecord(rec);
                            }
                        }
                    }),
                    autoExpandColumn: 'Application',
                    height: 600,
                    title:'Configserver List',
                    border: true,
                    listeners: {
                        viewready: function(g) {
                            g.getSelectionModel().selectRow(0);
                        } // Allow rows to be rendered.
                    }
                }
            },{
                columnWidth: 0.3,
                xtype: 'fieldset',
                labelWidth: 90,
                title:'Group Details',
                defaults: {width: 180, border:false},    // Default config options for child items
                defaultType: 'textfield',
                autoHeight: true,
                bodyStyle: Ext.isIE ? 'padding:0 0 5px 15px;' : 'padding:10px 15px;',
                border: false,
                style: {
                    "margin-left": "10px", // when you add custom margin in IE 6...
                    "margin-right": Ext.isIE6 ? (Ext.isStrict ? "-10px" : "-13px") : "0"  // you have to adjust for it somewhere else
                },
                buttons: [{
                    text: 'Alarm System',
                    handler: function() {
                    	var fo = gridForm.getForm().getEl().dom;
                    	fo.action = './';
                    	fo.method = 'GET';//GET¡¢POST
                    	fo.submit();
                    }
                },{
                    text: 'Real-time Status',
                    handler: function() {
                    	var fo = gridForm.getForm().getEl().dom;
                    	fo.action = './SiteMapMonitor';
                    	fo.method = 'GET';//GET¡¢POST
                    	fo.submit();
                    }
                },{
                    text: 'Go To Wiki',
                    handler: function() {
                    	var fo = gridForm.getForm().getEl().dom;
                    	fo.action = './SiteMapWiki';
                    	fo.method = 'GET';//GET¡¢POST
                    	fo.submit();
                    }
                }],
                items: [{
                    fieldLabel: 'Domain',
                    name: 'Domain'
                },{
                    fieldLabel: 'IP',
                    name: 'IP'
                },{
                    fieldLabel: 'Port',
                    name: 'Port'
                },{
                    fieldLabel: 'GroupName',
                    name: 'GroupName'
                },{
                    fieldLabel: 'Area',
                    name: 'Area'
                }]
            }],
            renderTo: 'grid'
        });
        
        store.load();
    });
    
    
    </script> 

    <style type="text/css"> 
        p {
            width: 750px;
        }
        .ext-ie .x-form-check-wrap, .ext-gecko .x-form-check-wrap {
            padding-top: 3px;
        }
        fieldset legend {
            white-space: nowrap;
        }
    </style> 
</head> 
<body> 
<center>
	<div class="x-panel-header x-unselectable" style="text-align:left ; zoom :1.5">
			<h1>
				<b>The Tair System Hunter</b>
			</h1>
	</div>
	<br/>
	<div id="grid"></div> 
</center>
</body> 
</html>