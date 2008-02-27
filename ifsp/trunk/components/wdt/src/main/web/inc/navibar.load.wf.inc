	<h:form>      
	
		<!-- All Navigation Stuff Here -->
		
		<div id="navbar">
			
		<ul class="primary">
			<li><h:commandLink value="[refresh]" action="#{templateContainerBean.loadTemplates}" /></li>
			<!--li><a class="navbar" href="/about/" title="1">Charakterisation</a></li-->
		</ul>
		
		
		<!-- HTML TABLE-->				
		<!--h:panelGrid id="templateLinkList" columns="1" styleClass="primary" columnClasses="primary" -->
			<!--h:commandLink value="Charakterisation" action="#{templateContainerBean.visualizeTemplate}" /-->
  		<!--h:outputText value="4" /-->
		<!--/h:panelGrid-->
		
		<ul class="primary">
			<t:dataList id="templateLinkList" var="template" value="#{templateContainerBean.templates}" layout="simple" rowCountVar="rowCount" rowIndexVar="rowIndex">
				<t:htmlTag value="li">
					 <h:commandLink value="#{template}" action="selectView" actionListener="#{templateContainerBean.selectView}"/>
					 <!--h:outputText value=", " rendered="#{rowIndex + 1 < rowCount}" /-->
				</t:htmlTag>
			</t:dataList> 
		</ul>
		
		<ul class="primary">
			<li><h:commandLink value="[lookup]" action="#{templateContainerBean.testRegistry}" /></li>
		</ul>		
		
		</div>
		
		
		
	</h:form>      


