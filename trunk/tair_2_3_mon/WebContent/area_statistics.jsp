<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Area Statistics Info</title>
<link rel="stylesheet" type="text/css"
	href="./resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="./ux/css/PanelResizer.css" />
<!-- overrides to base library -->
<link rel="stylesheet" type="text/css"
	href="./ux/gridfilters/css/GridFilters.css" />
<link rel="stylesheet" type="text/css"
	href="./ux/gridfilters/css/RangeMenu.css" />
<style type="text/css">
</style>

<!-- GC -->
<!-- LIBS -->
<script type="text/javascript" src="./adapter/ext/ext-base.js"></script>
<!-- ENDLIBS -->

<script type="text/javascript" src="./ext-all.js"></script>

<!-- extensions -->
<script type="text/javascript" src="./ux/gridfilters/menu/RangeMenu.js"></script>
<script type="text/javascript" src="./ux/gridfilters/menu/ListMenu.js"></script>

<script type="text/javascript" src="./ux/gridfilters/GridFilters.js"></script>
<script type="text/javascript" src="./ux/gridfilters/filter/Filter.js"></script>
<script type="text/javascript"
	src="./ux/gridfilters/filter/StringFilter.js"></script>
<script type="text/javascript"
	src="./ux/gridfilters/filter/DateFilter.js"></script>
<script type="text/javascript"
	src="./ux/gridfilters/filter/ListFilter.js"></script>
<script type="text/javascript"
	src="./ux/gridfilters/filter/NumericFilter.js"></script>
<script type="text/javascript"
	src="./ux/gridfilters/filter/BooleanFilter.js"></script>

<script type="text/javascript" src="./ux/ProgressBarPager.js"></script>
<script type="text/javascript" src="./ux/PanelResizer.js"></script>
<script type="text/javascript" src="./ux/PagingMemoryProxy.js"></script>

</head>
<body>
	<script type="text/javascript">
		Ext.onReady(function() {

			Ext.QuickTips.init();

			// configure whether filter query is encoded or not (initially)
			var encode = false;

			// configure whether filtering is performed locally or remotely (initially)
			var local = true;

			store = new Ext.data.JsonStore({
				// store configs
				autoDestroy : true,
				url : 'areainfo2json',
				remoteSort : false,
				sortInfo : {
					field : 'area',
					direction : 'ASC'
				},
				storeId : 'myStore',

				// reader configs
				root : 'root',
				totalProperty : 'totalproperty',
				fields : [ {
					name : 'area'
				}, {
					name : 'dataSize'
				}, {
					name : 'evictCount'
				}, {
					name : 'getCount'
				}, {
					name : 'hitRate'
				},{
					name : 'putCount'
				}, {
					name : 'hitCount'
				}, {
					name : 'itemCount'
				}, {
					name : 'removeCount'
				}, {
					name : 'useSize'
				},{
					name : 'quota'
				},{
					name : 'avgSize'
				}  ]
			});

			var filters = new Ext.ux.grid.GridFilters({
				// encode and local configuration options defined previously for easier reuse
				encode : encode, // json encode the filter query
				local : local, // defaults to false (remote filtering)
				filters : [ {
					type : 'numeric',
					dataIndex : 'area'
				}, {
					type : 'numeric',
					dataIndex : 'dataSize'
				}, {
					type : 'numeric',
					dataIndex : 'evictCount'
				}, {
					type : 'numeric',
					dataIndex : 'getCount'
				},{
					type : 'numeric',
					dataIndex : 'hitRate'
				},{
					type : 'numeric',
					dataIndex : 'putCount'
				},{
					type : 'numeric',
					dataIndex : 'hitCount'
				}, {
					type : 'numeric',
					dataIndex : 'itemCount'
				}, {
					type : 'numeric',
					dataIndex : 'removeCount'
				}, {
					type : 'numeric',
					dataIndex : 'useSize'
				}, {
					type : 'numeric',
					dataIndex : 'quota'
				}, {
					type : 'numeric',
					dataIndex : 'avgSize'
				}  ]
			});

			// use a factory method to reduce code while demonstrating
			// that the GridFilter plugin may be configured with or without
			// the filter types (the filters may be specified on the column model
			var createColModel = function(finish, start) {

				var columns = [ {
					dataIndex : 'area',
					header : 'area',
					id : 'area',
					filterable : true,
					filter : {
						type : 'numeric'
					}
				}, {
					header : 'evictCount/s',
					dataIndex : 'evictCount',
					filter : {}
				}, {
					header : 'getCount/s',
					dataIndex : 'getCount',
					filter : {}
				},{
					header : 'hitRate',
					dataIndex : 'hitRate'
				},{
					header : 'putCount/s',
					dataIndex : 'putCount',
					filter : {}
				},{
					header : 'hitCount/s',
					dataIndex : 'hitCount',
					filter : {}
				}, {
					header : 'itemCount',
					dataIndex : 'itemCount',
					// align : 'right',
					xtype: "numbercolumn",
					format : '0,000',
					filter : {}
				}, {
					header : 'removeCount/s',
					dataIndex : 'removeCount',
					filter : {}
				}, {
					header : 'dataSize(byte)',
					dataIndex : 'dataSize',
					// align : 'right',
					xtype: "numbercolumn",
					format : '0,000',
					filter : {}
				}, {
					header : 'useSize(byte)',
					dataIndex : 'useSize',
					// align : 'right',
					xtype: "numbercolumn",
					format : '0,000',
					filter : {}
				}, {
					header : 'Quota(byte)',
					dataIndex : 'quota',
					filter : {}
				}, {
					header : 'AvgSize(byte)',
					dataIndex : 'avgSize',
					filter : {}
				} ];

				return new Ext.grid.ColumnModel({
					columns : columns.slice(start || 0, finish),
					defaults : {

						sortable : true
					}
				});
			};

			var grid = new Ext.grid.GridPanel({
				border : true,
				stripeRows : true,
				frame : true,
				height : 600,
				width : 1124,
				store : store,
				colModel : createColModel(12, 0),
				loadMask : true,
				plugins : [ filters ],
				autoExpandColumn : 'area',
				// ## force to display every column ##
				//viewConfig : {
				//	forceFit : true
				//},
				listeners : {
					render : {
						fn : function() {
							store.load({
								params : {
									start : 0,
									limit : 65535
								}
							});
						}
					}
				}
			});

			grid.render('grid-online-list');
			var updateData = function() {
				store.reload();
			}
			var task = {
				run : updateData,
				interval : 60000
			//45 seconds
			}
			var runner = new Ext.util.TaskRunner();
			runner.start(task);

		});
	</script>
	<center>
		<jsp:include page="__common_head.jsp"></jsp:include>
		<div>Click Tabs Below To Set Filter On This Data</div>
		<div id="grid-online-list"></div>
	</center>

</body>
</html>