<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%/*@ taglib uri="http://java.sun.com/jsf/facelets" prefix="ui"*/%>
<%@include file="inc/menu.inc" %>

<f:view>

	<!-- navibar -->
	<%@include file="inc/navibar.filechooser.wf.inc" %>
	
	<!-- div class="content">
	<h1>IF/5 Online Workflow Design Tool</h1>
	<br>
	... I am a file chooser
	<br><br><br-->
	<f:loadBundle basename="eu.planets_project.ifr.core.wdt.gui.UIResource" var="res"/>
	
  	
 <div class="content"> 	
 																											
 <h:form id="drBrowserForm">

 <div id="mainwrapper">
 	
 <p> 
 		<h:outputText value="#{res['browse_data.browsing']}"/> 
 		<h:commandLink value="#{fileBrowser.rootUrl}" actionListener="#{fileBrowser.redirect}"/>
 </p>
                
 <div class="filepane">
 <div class="filebrowse">
  	<t:tree2 id="filerTree" value="#{fileBrowser.filerTree}" var="do" varNodeToggler="t" showRootNode="true" showLines="true" preserveToggle="true" clientSideToggle="true">
    
	    <f:facet name="folder">
	    	<h:panelGroup>
	      	<h:commandLink action="success">
	        <t:graphicImage value="/graphics/filetype_folder.gif" border="0" />
	        <h:outputText value=" #{do.leafname}"/>
	        <t:updateActionListener property="#{fileBrowser.location}" value="#{do.uri}" />
	        </h:commandLink>
	      </h:panelGroup>
	    </f:facet>
	    
	    <f:facet name="file">
	    	<h:panelGroup>
	      	<!-- ${facesContext.externalContext.requestContextPath} -->
	      	<t:graphicImage value="/graphics/filetype_file.png" title="directory" alt="directory" border="0" />
	      	<h:outputText value=" #{do.leafname}"/>
	      </h:panelGroup>
	    </f:facet>
    
    </t:tree2>
  </div>
<div class="filemain">
<!--div id="dataRegistryBrowser"-->
<p>
<!-- data table-->
	<t:dataTable id="FileBrowserList" var="do" value="#{fileBrowser.list}">
                
    <h:column>
      <h:selectBooleanCheckbox value="#{do.selected}" rendered="#{do.selectable}"/>
    </h:column>
           
    <h:column>
    	<f:facet name="header">#{res['browse_data.name']}</f:facet>
    
	    <h:panelGroup rendered="#{do.directory}">
	      <h:commandLink action="success">
	        <!-- #{facesContext.externalContext.requestContextPath} -->
	        <t:graphicImage value="/graphics/filetype_folder.gif" title="directory" alt="directory"/>
	        <h:outputText value=" #{do.leafname}"/>
	        <t:updateActionListener property="#{fileBrowser.location}" value="#{do.uri}" />
	      </h:commandLink>
	    </h:panelGroup>
	    
    	<h:panelGroup rendered="#{not do.directory}">
      	<!-- #{facesContext.externalContext.requestContextPath} -->
      	<t:graphicImage value="/graphics/filetype_file.png" />
      	<h:outputText value=" #{do.leafname}"/>
    	</h:panelGroup>
    	  	
    </h:column>
    
	</t:dataTable>
	
                
	<h:commandButton action="#{fileBrowser.selectAll}" value="#{res['browse_data.selectAll']}"/>
  <h:commandButton action="#{fileBrowser.selectNone}" value="#{res['browse_data.selectNone']}"/>
                
                <!-- /p -->
                <!-- h:panelGroup rendered="#{isInExperiment}"-->
                <!--p --> 
	<h:commandButton action="#{fileBrowser.addToExperiment}" value="#{res['browse_data.addToExperiment']}"/>
                <!--/p-->
                <!--p-->
                <!--h:commandButton action="goToStage2"
                  value="#{res['browse_data.returnToExperiment']}"/-->
                <!--/p-->
                <!--/h:panelGroup--> 
                
    </div>
   </div>
  </div>
  
  <br><br>
  
</h:form>
  
          

</f:view>
<%@include file="inc/footer.inc" %>