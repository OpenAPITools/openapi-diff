package com.qdesrame.openapi.diff.model;

import io.swagger.oas.models.parameters.Parameter;


public class ChangedParameter implements Changed {
	private Parameter left;
	private Parameter right;

	private String name;
	private String in;
	private boolean changeRequired;
	private boolean changeDescription;
	private boolean deprecated;
	private ChangedSchema changedSchema;

	public ChangedParameter(String name, String in) {
		this.name = name;
		this.in = in;
	}

	@Override
	public boolean isDiff() {
		return false;
	}

	public void setChangeRequired(boolean changeRequired) {
		this.changeRequired = changeRequired;
	}

	public boolean isChangeRequired() {
		return changeRequired;
	}

	public void setChangeDescription(boolean changeDescription) {
		this.changeDescription = changeDescription;
	}

	public boolean isChangeDescription() {
		return changeDescription;
	}

	public void setChangeDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}

	public void setChangedSchema(ChangedSchema changedSchema) {
		this.changedSchema = changedSchema;
	}

	public Parameter getLeftParameter() {
		return left;
	}

	public void setLeftParameter(Parameter left) {
		this.left = left;
	}

	public Parameter getRightParameter() {
		return right;
	}

	public void setRightParameter(Parameter right) {
		this.right = right;
	}

}
