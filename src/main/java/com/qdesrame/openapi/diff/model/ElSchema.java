package com.qdesrame.openapi.diff.model;

import io.swagger.v3.oas.models.media.Schema;

/**
 * property with expression Language grammar
 * @author QDesrame
 * @version 
 */
public class ElSchema {

	private String el;

	private Schema schema;

	public Schema getSchema() {
		return schema;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	public String getEl() {
		return el;
	}

	public void setEl(String el) {
		this.el = el;
	}

}
