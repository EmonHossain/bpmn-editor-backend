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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Component
public class XMLWriterDom implements XMLConstants {

	private Resource resource;

	private Element edgeElement;
	private TransformerFactory transformerFactory;
	private Transformer transformer;
	private DOMSource domSource;
	private StreamResult streamResult;

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	public XMLWriterDom prepareXml(String xml, List<XMLProperties> listOfProperties) {
		Document doc = this.normalizeXML(xml);

		for (XMLProperties p : listOfProperties) {

		}

		return this;
	}

	private Document normalizeXML(String xml) {
		Document doc = null;
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.parse(IOUtils.toInputStream(xml));
			doc.getDocumentElement().normalize();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return doc;
	}

	private void buildNode(Document doc, XMLProperties xmlProperties) {
		Element elem = doc.getElementById(xmlProperties.getId());

		Map<String, String> map = this.extractValues(xmlProperties.getData(), ",");

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

	private void writeXmlDocument(Document doc) throws SAXException, IOException {
		try {
			this.transformerFactory = TransformerFactory.newInstance();
			this.transformer = this.transformerFactory.newTransformer();
			this.domSource = new DOMSource(doc);
			// resource = new
			// ClassPathResource("/tempProcess/Temp_loanProcess.xml");
			LOG.debug("URI " + resource.getURI());
			LOG.debug("URL " + resource.getURL());
			this.streamResult = new StreamResult(new File(resource.getURI()));
			StreamResult result = new StreamResult(System.out);
			transformer.transform(domSource, streamResult);
			transformer.transform(domSource, result);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
