<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<%@ include file="../inc/menu.inc" %>
<%@ include xmlns:ui="http://java.sun.com/jsf/facelets"%>
<%@ page import="eu.planets_project.ifr.core.wdt.gui.faces.TemplateContainer" %>

<f:view>


<%
    // Check if the bean is already created or not.
    // For request scoped beans of course use request.get/setAttribute() instead.
    //if (session.getAttribute("templateContainer") == null) {
        // First-time initialization of bean not done yet, so do it manually.
    //    session.setAttribute("templateContainer", new TemplateContainer());
    //}
%>


	<!-- navibar -->
	<%@include file="../inc/navibar.load.wf.inc" %>	
	<ui:include src="/views/wf.characterization.jsp">
	
	<!--h:outputText value="Hello currentView: #{templateContainer.currentView}" /-->
		
	<!--%TemplateContainer container = (TemplateContainer) session.getAttribute( "templateContainer" ); %-->
	<!--%= "Hello containerBean: "+container+"\n"%-->
	
	<!--h:panelGrid rendered="#{templateContainer.currentView != null}"--><!--h:panelGroup--> 
	<!--f:subview id="sub0"-->
			<!--verbatim><%= "Hello panel"%></verbatim-->
			<!--h:outputText value="Hello jsf output text"/-->
			<!--jsp:include page="${sessionScope.templateContainer.currentView}" /-->
			<!--jsp:include page="<%= container.getCurrentView() %>"/-->
	<!--/f:subview-->
		<!--/h:panelGroup--><!--/h:panelGrid-->
	<!--%= "after panel"%-->


</f:view>

<%@include file="../inc/footer.inc" %>