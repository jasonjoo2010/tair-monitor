<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Query A Key</title>

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

			    var fs = new Ext.FormPanel({
			        frame: true,
			        title:'C++ Query',
			        labelAlign: 'right',
			        labelWidth: 120,
			        width:1024,
			        waitMsgTarget: true,

			        // configure how to read the XML Data
			        reader : new Ext.data.JsonReader( 
		        		{                     
		        			totalProperty: "totalProperty",  //totalRecords属性由json.results得到                     
		        			successProperty: true,    //json数据中，保存是否返回成功的属性名                     
		        			root: "root"           //构造元数据的数组由json.rows得到                               
		        		}, 	
        			[{ name: 'Area' },       //如果name与mapping同名,可以省略mapping  
        		   	 { name: 'Key' },
        		   	 { name: 'Value' }] 
		             ),

			        items: [
			            new Ext.form.FieldSet({
			                title: 'Contact Information',
			                autoHeight: true,
			                defaultType: 'textfield',
			                items: [ {
			                        fieldLabel: 'Area',
			                        emptyText: '',
			                        name: 'Area',
			                        width:830
			                    },  {
			                        fieldLabel: 'Key',
			                        emptyText: 'Key',
			                        name: 'Key',
			                        width:830
			                    },	{
			                        fieldLabel: 'Value',
			                        emptyText: '',
			                        //disabled:true,
			                        name: 'Value',
			                        width:830
			                    }
			                ]
			            })
			        ]
			    });

			    // simple button add
			    fs.addButton('Load', function(){
			    	content = 'getrequestnoheader?Area='+fs.getForm().findField('Area').getValue()+
			    			  '&Key='+escape(escape(fs.getForm().findField('Key').getValue()).replace(/\+/g, '%2B'));
			        fs.getForm().load({
			        	url: content,
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