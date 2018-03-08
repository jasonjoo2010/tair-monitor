<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page
	import="com.tair_2_3.statmonitor.*,java.util.*"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Area Statistics Info</title>
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
	<script type="text/javascript">
		Ext.onReady(function() {
					Ext.QuickTips.init();
					
					
					var myData = <%
		            	 int area = 0;
	            	 try {
	            	 	area = Integer.parseInt(request.getParameter("area"));
	            	 } catch (Exception e) {
	            	 }
	            	 String output = "";

	            	 	HashMap<Integer, AreaStatistics> _area_statistics =TairStatInfoReaderDeamon.getTask().get_aggregate_area_statistics();
	            	 	String buf="";
	            	 	if(_area_statistics.containsKey(area)) {
	            	 		AreaStatistics tem = _area_statistics.get(area);
	            	 		buf += "[";
	            	 		buf +=area + "," 
	            	 				+ tem.getDataSize() + ","
	            	 				+ tem.getEvictCount() + ","
	            	 				+ tem.getGetCount() + ","
	            	 				+ (tem.getGetCount()==0 ? 0 : (new java.text.DecimalFormat( "#.## ")).format(((double)tem.getHitCount() / tem.getGetCount()))) + ","
	            	 				+ tem.getPutCount() + "," 
	            	 				+ tem.getHitCount() + "," 
	            	 				+ tem.getItemCount() + "," 
	            	 				+ tem.getRemoveCount() + "," 
	            	 				+ tem.getUseSize() + "," 
	            	 				+ tem.getQuota() ;
	            	 		buf += "],\n";
	            	 		output = "[" + buf.substring(0, buf.length()-2) + "]";
	            	 		out.print(output);
	            	 	}
	            	 	%>;

					// create the data store
					var store = new Ext.data.ArrayStore({
						fields : [ {
							name : 'area'
						}, {
							name : 'dataSize',
							type: 'float'
						}, {
							name : 'evictCount',
							type: 'float'
						}, {
							name : 'getCount',
							type: 'float'
						}, {
							name : 'hitRate',
							type: 'float'
						},{
							name : 'putCount',
							type: 'float'
						}, {
							name : 'hitCount',
							type: 'float'
						}, {
							name : 'itemCount',
							type: 'float'
						}, {
							name : 'removeCount',
							type: 'float'						
						}, {
							name : 'useSize',
							type: 'float'
						}, {
							name : 'quota',
							type: 'float'
						} ]
					});
				store.loadData(myData);
					// create the Grid
					var grid = new Ext.grid.GridPanel({
						store : store,
						columns : [ {
							header :'area',
							dataIndex : 'area',
							sortable : true ,
							width : 100
						}, {
							header :'evictCount/s',
							dataIndex : 'evictCount',
							width : 80
						}, {
							header :'getCount/s',
							dataIndex : 'getCount',
							width : 80
						}, {
							header :'hitRate',
							dataIndex : 'hitRate',
							width : 80
						}, {
							header :'putCount/s',
							dataIndex : 'putCount',
							width : 80
						}, {
							header :'hitCount/s',
							dataIndex : 'hitCount',
							width : 80
						}, {
							header :'itemCount',
							dataIndex : 'itemCount',
							width : 100
						}, {
							header :'removeCount/s',
							dataIndex : 'removeCount',
							width : 100
						}, {
							dataIndex : 'dataSize',
							header :'dataSize(byte)',
							width : 100
						}, {
							header :'useSize(byte)',
							dataIndex : 'useSize',
							width : 100
						}, {
							header :'Quota(byte)',
							dataIndex : 'quota',
							width : 100
						} ],
						height : 350,
						width : 1124,
						autoExpandColum : 'area' ,
						stripeRows : true
					});
				grid.render('grid-online-list');
				});

	</script>
	<center>
		<jsp:include page="__common_head.jsp"></jsp:include>
		<div id="grid-online-list"></div>
	</center>

</body>
</html>