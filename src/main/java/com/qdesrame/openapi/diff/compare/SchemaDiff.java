package com.qdesrame.openapi.diff.compare;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.qdesrame.openapi.diff.model.ElSchema;

import io.swagger.oas.models.media.Schema;

/**
 * compare two model
 * @author QDesrame
 * @version 
 */
public class SchemaDiff {

	private List<ElSchema> increased;
	private List<ElSchema> missing;

	private Map<String, Schema> oldDefinitions;
	private Map<String, Schema> newDefinitions;

	private SchemaDiff() {
		increased = new ArrayList<ElSchema>();
		missing = new ArrayList<ElSchema>();
	}

	public static SchemaDiff buildWithDefinition(Map<String, Schema> left,
												 Map<String, Schema> right) {
		SchemaDiff diff = new SchemaDiff();
		diff.oldDefinitions = left;
		diff.newDefinitions = right;
		return diff;
	}

	public SchemaDiff diff(Schema leftSchema, Schema rightSchema) {
		return this.diff(leftSchema, rightSchema, null);
	}

	public SchemaDiff diff(Schema leftSchema, Schema rightSchema, String parentEl) {
		if (null == leftSchema && null == rightSchema) return this;
		Map<String, Schema> leftProperties = null == leftSchema ? null : leftSchema.getProperties();
		Map<String, Schema> rightProperties = null == rightSchema ? null : rightSchema.getProperties();
		MapKeyDiff<String, Schema> propertyDiff = MapKeyDiff.diff(leftProperties, rightProperties);
		Map<String, Schema> increasedProp = propertyDiff.getIncreased();
		Map<String, Schema> missingProp = propertyDiff.getMissing();

		increased.addAll(convert2ElPropertys(increasedProp, parentEl, false));
		missing.addAll(convert2ElPropertys(missingProp, parentEl, true));

		List<String> sharedKey = propertyDiff.getSharedKey();
		for (String key : sharedKey) {
			Schema left = leftProperties.get(key);
			Schema right = rightProperties.get(key);
			if (left.get$ref() != null
					&& right.get$ref() != null) {
				String leftRef = left.get$ref();
				String rightRef = right.get$ref();
				diff(oldDefinitions.get(leftRef),
						newDefinitions.get(rightRef),
						null == parentEl ? key : (parentEl + "." + key));
			}
		}
		return this;
	}

	private Collection<? extends ElSchema> convert2ElPropertys(
			Map<String, Schema> propMap, String parentEl, boolean isLeft) {
		List<ElSchema> result = new ArrayList<ElSchema>();
		if (null == propMap) return result;
		for (Entry<String, Schema> entry : propMap.entrySet()) {
			String propName = entry.getKey();
			Schema property = entry.getValue();
			if (property.get$ref() != null) {
				String ref = property.get$ref();
				Schema schema = isLeft ? oldDefinitions.get(ref)
						: newDefinitions.get(ref);
				if (schema != null) {
					Map<String, Schema> properties = schema.getProperties();
					result.addAll(
							convert2ElPropertys(properties,
									null == parentEl ? propName
											: (parentEl + "." + propName),
									isLeft));
				}
			} else {
				ElSchema pWithPath = new ElSchema();
				pWithPath.setSchema(property);
				pWithPath.setEl(null == parentEl ? propName
						: (parentEl + "." + propName));
				result.add(pWithPath);
			}
		}
		return result;
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
