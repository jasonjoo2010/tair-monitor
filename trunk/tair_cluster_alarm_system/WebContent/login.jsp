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
            var form= new Ext.form.FormPanel({
                labelAlign:'center',
                title:'µÇÂ¼',
                labelWidth:50,
                frame:true,
                width:300,
                items: [{    
                        xtype:'textfield',
                        fieldLabel: 'ÓÃ»§Ãû',
                        name:'username',
                        id:'username'
                      },
                    {    
                        xtype:'textfield',
                        inputType:'password',
                        fieldLabel: 'ÃÜÂë',
                        name:'password',
                        id:'password'
                      },
                      {
                          xtype:'panel',
                          html:'<center><%=(request.getParameter("tip")==null?"":request.getParameter("tip")) %></center>'
                      }
                ],
                buttons: [{
                    text: 'µÇÂ¼',
                    handler: function() {
                    	var fo = form.getForm().getEl().dom;
                        fo.action = './index.jsp';
                        fo.method = 'GET';//GET¡¢POST
                        fo.submit();

                    }
                }]
                
            });
            
            form.render('form');
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
<div id="form"></div> 
</center>
</body> 
</html>