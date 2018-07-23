package com.bpmneditor.core;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BpmnData {
	private String xml;
	private List<XMLProperties> properties;

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public List<XMLProperties> getProperties() {
		return properties;
	}

	public void setProperties(List<XMLProperties> properties) {
		this.properties = properties;
	}

	@Override
	public String toString() {
		return "BpmnData [xml=" + xml + ", properties=" + properties + "]";
	}

}
