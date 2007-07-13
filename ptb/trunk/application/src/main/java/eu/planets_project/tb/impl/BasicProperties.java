package eu.planets_project.tb.impl;

import java.util.Hashtable;
import java.util.Iterator;

import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.model.User;

//EJB 3.0. Entity Bean
import javax.persistence.*;

//@Entity
public class BasicProperties implements
		eu.planets_project.tb.api.model.BasicProperties {

	public String getConsiderations() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getContactAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getContactMail() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getContactName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getContactTel() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getExperimentApproach() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getExperimentApproach(int iid) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getExperimentName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Iterator<Long> getExperimentReferences() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getExperimentedObjectType() {
		// TODO Auto-generated method stub
		return null;
	}

	public Iterator<String> getExperimentedObjectTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getFocus() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getIndication() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPurpose() {
		// TODO Auto-generated method stub
		return null;
	}

	public long[] getReferencedExperimentIDs() {
		// TODO Auto-generated method stub
		return null;
	}

	public Experiment[] getReferencedExperiments() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getScope() {
		// TODO Auto-generated method stub
		return null;
	}

	public void getSpecificFocus() {
		// TODO Auto-generated method stub

	}

	public String getSummary() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setConsiderations(String consid) {
		// TODO Auto-generated method stub

	}

	public void setContact(String name, String mail, String tel, String address) {
		// TODO Auto-generated method stub

	}

	public void setContact(User bean) {
		// TODO Auto-generated method stub

	}

	public void setExperimentApproach(int iid) {
		// TODO Auto-generated method stub

	}

	public void setExperimentName(String name) {
		// TODO Auto-generated method stub

	}

	public void setExperimentReference(long refID) {
		// TODO Auto-generated method stub

	}

	public void setExperimentReference(Experiment refExp) {
		// TODO Auto-generated method stub

	}

	public void setExperimentReferences(long[] refIDs) {
		// TODO Auto-generated method stub

	}

	public void setExperimentReferences(Experiment[] refExps) {
		// TODO Auto-generated method stub

	}

	public void setExperimentedObjectType(String mimeType) {
		// TODO Auto-generated method stub

	}

	public void setExperimentedObjectTypes(String[] mimeTypes) {
		// TODO Auto-generated method stub

	}

	public void setExperimenter(User experimenter) {
		// TODO Auto-generated method stub

	}

	public void setExperimenter(long userID) {
		// TODO Auto-generated method stub

	}

	public void setFocus(String focus) {
		// TODO Auto-generated method stub

	}

	public void setIndication(String description) {
		// TODO Auto-generated method stub

	}

	public void setInvolvedUsers(User[] users) {
		// TODO Auto-generated method stub

	}

	public void setInvolvedUsers(long userID) {
		// TODO Auto-generated method stub

	}

	public void setInvolvedUsers(
			Hashtable<Long, Integer> userIDsAndExperimentRoles) {
		// TODO Auto-generated method stub

	}

	public void setPurpose(String purpose) {
		// TODO Auto-generated method stub

	}

	public void setScope(String scope) {
		// TODO Auto-generated method stub

	}

	public void setSpecificFocus(String focus) {
		// TODO Auto-generated method stub

	}

	public void setSummary(String summary) {
		// TODO Auto-generated method stub

	}

	public Iterator<String> getAllAvailableInstitutions() {
		// TODO Auto-generated method stub
		return null;
	}

	public Iterator<String> getAllAvailablePartnerTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getInstitutionsID(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getInstitutionsName(int institutionID) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getInstitutionsParnterType(int institutionID) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getInstitutionsPartnerTypeDescription(int institutionID) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getTypeID(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getTypeNames(int partnerTypeID) {
		// TODO Auto-generated method stub
		return null;
	}

}
