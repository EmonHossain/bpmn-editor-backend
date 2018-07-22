package com.bpmneditor.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

@Component
public class XMLWriterDom implements XMLConstants {
	
	private Document doc;

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	public XMLWriterDom prepareXml(String xml, List<XMLProperties> listOfProperties) {
		this.normalizeXML(xml);
		for (XMLProperties p : listOfProperties) {
			this.buildNode(p);
		}
		return this;
	}

	private void normalizeXML(String xml) {
		
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.parse(IOUtils.toInputStream(xml));
			doc.getDocumentElement().normalize();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void buildNode(XMLProperties xmlProperties) {
		Element elem = doc.getElementById(xmlProperties.getId());

		Map<String, String> map = extractValues(xmlProperties.getData(), ",");

		switch (elem.getTagName()) {
		case ELEMENT_TASK_USER:
			elem.setAttribute(ACTIVITI_EXTENSIONS_PREFIX + COLON + ATTRIBUTE_TASK_USER_ASSIGNEE,
					xmlProperties.getData());
			elem.setAttribute(ACTIVITI_EXTENSIONS_PREFIX + COLON + ATTRIBUTE_TASK_USER_CATEGORY, "");
			elem.setAttribute(ACTIVITI_EXTENSIONS_PREFIX + COLON + ATTRIBUTE_NAME, "");
			break;
		case ELEMENT_GATEWAY_EXCLUSIVE:
		case ELEMENT_GATEWAY_INCLUSIVE:
		case ELEMENT_GATEWAY_PARALLEL:
		case ELEMENT_SEQUENCE_FLOW:
			Element e = (Element) doc.createElement(ELEMENT_FLOW_CONDITION);
			e.setAttribute(ATTRIBUTE_TYPE + COLON + ATTRIBUTE_TYPE, "tFormalExpression");
			e.appendChild(doc.createCDATASection(""));
			elem.appendChild(e);

		default:
			break;
		}
	}

	public static Map<String, String> extractValues(String data, String delim) {
		Map<String, String> m = new HashMap<String, String>();
		StringTokenizer tokenizer = new StringTokenizer(data, delim);
		while (tokenizer.hasMoreTokens()) {
			String[] part = tokenizer.nextToken().toString().split(":");
			m.put(part[0], part[1]);
		}
		return m;
	}

	public void writeToXmlDocument(String destination) throws SAXException, IOException {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(doc);
			StreamResult streamResult = new StreamResult(new File(destination));
			transformer.transform(domSource, streamResult);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
