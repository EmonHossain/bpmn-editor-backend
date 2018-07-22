package com.bpmneditor.core;

public class XMLProperties {
	private String id;
	private String type;
	private String data;

	public XMLProperties() {

	}

	public XMLProperties(String id, String type, String data) {
		this.id = id;
		this.type = type;
		this.data = data;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
