<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@include file="../inc/menu.inc" %>

<f:view>

	<!-- navibar -->
	<%@include file="../inc/navibar.load.wf.inc" %>
	
	<div class="content">
	<h1>Characterization Workflow Template</h1>
	<br>
	<br>
	<h:panelGrid columns="3" align="center">
  <!--f:facet name="header"-->
    <!-- h:outputText value="Table with numbers"/-->
  <!-- /f:facet-->
  <h:outputText value=" " />
  <t:htmlTag value="center">
  	<h:outputText value="X" />
  </t:htmlTag>
  <h:outputText value=" " />
  	
  <h:outputText value=" " />
  <t:htmlTag value="center">
		<h:selectOneMenu id="select_services1" required="true" value="#{characterizationWorkflowBean.currentCharService}">
  		<f:selectItems value="#{characterizationWorkflowBean.charServices}"/>
  	</h:selectOneMenu>				
  </t:htmlTag>
  <h:outputText value=" " />
  	
  <h:outputText value=" " />
  <t:htmlTag value="center">
  	<h:outputText value="|" />
	</t:htmlTag>  		
  <h:outputText value=" " />

  <h:outputText value=" " />
  <t:htmlTag value="center">
	  <h:selectOneMenu id="chooseAppleColor" value="apple">
	  	<f:selectItem itemValue="red" itemLabel="Red"/>
	  	<f:selectItem itemValue="blue" itemLabel="Blue"/>
	  	<f:selectItem itemValue="black" itemLabel="Black"/>
	  	<f:selectItem itemValue="green" itemLabel="Green"/>
	  	<f:selectItem itemValue="white" itemLabel="White"/>
	  </h:selectOneMenu> 
	</t:htmlTag>
  <h:outputText value=" " />

	<h:outputText value=" " />
	<t:htmlTag value="center">
  	<h:outputText value="Y" />
  </t:htmlTag>
  <h:outputText value=" " />
  	
	</h:panelGrid> 
	<br><br><br>

</f:view>

<%@include file="../inc/footer.inc" %>