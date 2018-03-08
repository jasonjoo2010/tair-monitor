<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="GB18030"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Query Ds By Key</title>

<link rel="stylesheet" type="text/css"
	href="./resources/css/ext-all.css" />
<style type="text/css">
</style>

<!-- GC -->
<!-- LIBS -->
<script type="text/javascript" src="./adapter/ext/ext-base.js"></script>
<!-- ENDLIBS -->

<script type="text/javascript" src="./ext-all.js"></script>

<!-- extensions -->

</head>
<body>
<center>
<script type="text/javascript">
		Ext.onReady(function() {
			
			Ext.QuickTips.init();

		    // turn on validation errors beside the field globally
		    Ext.form.Field.prototype.msgTarget = 'side';
		    
		    
			Ext.namespace('Ext.exampledata');
			Ext.exampledata.states = [
              [1, 'TAIR_STYPE_INT' ],
              [2, 'TAIR_STYPE_STRING' ],
              [3, 'TAIR_STYPE_BOOL'],
              [4, 'TAIR_STYPE_LONG'],
              [5, 'TAIR_STYPE_DATE'],
              [6, 'TAIR_STYPE_BYTE'],
              [7, 'TAIR_STYPE_FLOAT'],
              [8, 'TAIR_STYPE_DOUBLE'],
              [9, 'TAIR_STYPE_BYTEARRAY']
			                          ];
			
			Ext.exampledata.isprefix = [
			                          [1, 'YES' ],
			                          [2, 'NO' ],
			            			                          ];
	

			    var fs = new Ext.FormPanel({
			        frame: true,
			        title:'DataServer Query',
			        labelAlign: 'right',
			        labelWidth: 120,
			        width:1024,
			        waitMsgTarget: true,

			        // configure how to read the XML Data
			        reader : new Ext.data.JsonReader( 
		        		{                     
		        			totalProperty: "totalProperty",  //totalRecordsÊôÐÔÓÉjson.resultsµÃµ½                     
		        			successProperty: true,    //jsonÊý¾ÝÖÐ£¬±£´æÊÇ·ñ·µ»Ø³É¹¦µÄÊôÐÔÃû                     
		        			root: "root"           //¹¹ÔìÔªÊý¾ÝµÄÊý×éÓÉjson.rowsµÃµ½                               
		        		}, 	
        			[{ name: 'Key' },
        		   	// { name: 'Type' },
        		   	 { name: 'DataServer' }] 
		             ),

			        items: [
			            new Ext.form.FieldSet({
			                title: 'Contact Information',
			                autoHeight: true,
			                defaultType: 'textfield',
			                items: [ {
			                        fieldLabel: 'Key',
			                        emptyText: 'Key',
			                        name: 'Key',
			                        width:830
			                    },

			                    new Ext.form.ComboBox({
			                        fieldLabel: 'Data Type of Key',
			                        hiddenName:'Type',
			                        store: new Ext.data.ArrayStore({
			                            fields: ['type', 'typename'],
			                            data : Ext.exampledata.states // from states.js
			                        }),
			                        valueField:'type',
			                        displayField:'typename',
			                        typeAhead: true,
			                        mode: 'local',
			                        triggerAction: 'all',
			                        emptyText:'Select a type...',
			                        selectOnFocus:true,
			                        width:830
			                    }), 
			                    
			                    new Ext.form.ComboBox({
			                        fieldLabel: 'If Prefix Key',
			                        hiddenName:'IsPrefix',
			                        store: new Ext.data.ArrayStore({
			                            fields: ['IsPrefix', 'typename'],
			                            data : Ext.exampledata.isprefix // from states.js
			                        }),
			                        valueField:'IsPrefix',
			                        displayField:'typename',
			                        typeAhead: true,
			                        mode: 'local',
			                        triggerAction: 'all',
			                        emptyText:'Select a type...',
			                        selectOnFocus:true,
			                        width:830
			                    }),  
			                    
			                    {
			                        fieldLabel: 'DataServer',
			                        emptyText: '',
			                        //disabled:true,
			                        name: 'DataServer',
			                        width:830
			                    }

			                    //new Ext.form.DateField({
			                    //    fieldLabel: 'Date of Birth',
			                    //    name: 'dob',
			                    //    width:190,
			                    //    allowBlank:false
			                    //})
			                ]
			            })
			        ]
			    });

			    fs.getForm().findField('Type').setValue(4);
			    fs.getForm().findField('IsPrefix').setValue(1);
			    // simple button add
			    fs.addButton('Load', function(){
			        fs.getForm().load({
			        	url:'getdsrequest?Key='+
			        		escape(escape(fs.getForm().findField('Key').getValue()).replace(/\+/g, '%2B')) +
			        	    '&Type='+fs.getForm().findField('Type').getValue() + 
			        	    '&IsPrefix='+fs.getForm().findField('IsPrefix').getValue(), 
			        	waitMsg:'Loading'});
			    });
	
			    fs.render('form-ct');

	    
			 
		});
	</script>
	<jsp:include page="__common_head.jsp"></jsp:include>
<div id="form-ct"></div> 
</center>
</body>
</html>