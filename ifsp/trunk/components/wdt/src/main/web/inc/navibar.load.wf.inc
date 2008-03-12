	<h:form>      
	
		<!-- All Navigation Stuff Here -->
		
		<div id="navbar">
			
		<ul class="primary">
			<li><h:commandLink value="[ refresh templates ]" action="#{templateContainer.loadTemplates}" /></li>
			<!--li><a class="navbar" href="/about/" title="1">Charakterisation</a></li-->
		
			<ul class="secondary">
				<t:dataList id="templateLinkList" var="template" value="#{templateContainer.templates}" layout="simple" rowCountVar="rowCount" rowIndexVar="rowIndex">
					<t:htmlTag value="li">
						 <h:commandLink value="#{template}" action="selectTemplate" actionListener="#{templateContainer.selectTemplate}"/>
						 <!--h:outputText value=", " rendered="#{rowIndex + 1 < rowCount}" /-->
					</t:htmlTag>
				</t:dataList> 
			</ul>
		</ul>
		
		<ul class="primary">
			<li><h:commandLink value="[ lookup registry ]" action="#{templateContainer.testRegistry}" /></li>
		</ul>		
		
		</div>
		
	</h:form>      


