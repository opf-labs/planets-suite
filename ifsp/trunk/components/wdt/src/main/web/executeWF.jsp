<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@include file="inc/menu.inc" %>

<%@ page import = "eu.planets_project.ifr.core.wdt.gui.faces.TemplateContainer" %>

<f:view>

	<!-- navibar -->
	<%@include file="inc/navibar.execute.wf.inc" %>

	<%@include file="views/wf.characterization.jsp" %>
		

</f:view>

<%@include file="inc/footer.inc" %>