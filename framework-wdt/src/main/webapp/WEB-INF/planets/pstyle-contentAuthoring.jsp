<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:import url="/header.jsp" context="/">
    <c:param name="title" value="Data Registry Browser" />
    <c:param name="extra-css" value="css/styles.css"/>
</c:import>

<div class="content">

<f:view>

	<%@include file="includes/header.jsp"%>

	<h:panelGrid columns="2"
		columnClasses="pageColumnNavigation, pageColumnMain"
		style="width:100%; margin:0px;">

		<%@include file="/includes/nodeList.jsp"%>

		<t:panelTabbedPane serverSideTabSwitch="false"
			activeTabStyleClass="mainTabPanelActiveTab"
			inactiveTabStyleClass="mainTabPanelInactiveTab"
			disabledTabStyleClass="mainTabPanelDisabledTab"
			tabContentStyleClass="mainTabPanelContent"
			activeSubStyleClass="mainTabActiveSub">

			<t:panelTab id="tab1" label="Explore">
				<jsp:include page="/includes/mainNodePanel.jsp" />
			</t:panelTab>

			<t:panelTab id="tab2" label="Versions" rendered="#{ContentBean.currentNodeIsVersionable==true}">
				<jsp:include page="/includes/nodeVersionPanel.jsp" />
			</t:panelTab>
			
			<t:panelTab id="tab3" label="Lock" rendered="#{ContentBean.currentLock != null}">
				<jsp:include page="/includes/nodeLockPanel.jsp" />
			</t:panelTab>

		</t:panelTabbedPane>

	</h:panelGrid>
</f:view>

</div>

<c:import url="/footer.jsp" context="/" />
