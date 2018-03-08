<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>forwarding</title>
</head>
<body>
	<%
		com.tair_2_3.statmonitor.comm.MonitorAcl monitorAcl = com.tair_2_3.statmonitor.TairStatInfoReaderDeamon
				.getAclModule();
		if (monitorAcl.getNeedAclCheck() && !monitorAcl.CheckAccessStatus(request)) {
			javax.servlet.RequestDispatcher requestDispatcher;
			requestDispatcher = request
					.getRequestDispatcher("/aclfailed.jsp?applyUrl="
							+ com.tair_2_3.statmonitor.TairStatInfoReaderDeamon
									.getAclModule().getAclFailedJumpUrl());
			requestDispatcher.forward(request, response);
			return;
		}
	%>
<jsp:forward page="area_statistics.jsp"></jsp:forward>
</body>
</html>