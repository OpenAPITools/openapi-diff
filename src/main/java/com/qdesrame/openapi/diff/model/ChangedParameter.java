package com.qdesrame.openapi.diff.model;

import io.swagger.oas.models.parameters.Parameter;

import java.util.ArrayList;
import java.util.List;


public class ChangedParameter implements Changed {
	
	private List<ElSchema> increased = new ArrayList<ElSchema>();
	private List<ElSchema> missing = new ArrayList<ElSchema>();;

	private Parameter leftParameter;
	private Parameter rightParameter;

	private boolean isChangeRequired;
	// private boolean isChangeType;
	private boolean isChangeDescription;

	public boolean isChangeRequired() {
		return isChangeRequired;
	}

	public void setChangeRequired(boolean isChangeRequired) {
		this.isChangeRequired = isChangeRequired;
	}

	public boolean isChangeDescription() {
		return isChangeDescription;
	}

	public void setChangeDescription(boolean isChangeDescription) {
		this.isChangeDescription = isChangeDescription;
	}

	public Parameter getLeftParameter() {
		return leftParameter;
	}

	public void setLeftParameter(Parameter leftParameter) {
		this.leftParameter = leftParameter;
	}

	public Parameter getRightParameter() {
		return rightParameter;
	}

	public void setRightParameter(Parameter rightParameter) {
		this.rightParameter = rightParameter;
	}

	public boolean isDiff() {
		return isChangeRequired || isChangeDescription || !increased.isEmpty() || !missing.isEmpty();
	}

	public List<ElSchema> getIncreased() {
		return increased;
	}

	public void setIncreased(List<ElSchema> increased) {
		this.increased = increased;
	}

	public List<ElSchema> getMissing() {
		return missing;
	}

	public void setMissing(List<ElSchema> missing) {
		this.missing = missing;
	}
	

}
