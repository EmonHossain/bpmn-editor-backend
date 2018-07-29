package com.bpmneditor.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

@Component
public class XMLWriterDom implements XMLConstants {

	private Document doc;

	// private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	public void prepareXml(String xml, List<XMLProperties> listOfProperties, String destination)
			throws IOException, SAXException, XPathExpressionException {
		System.out.println(xml);
		this.normalizeXML(xml);
		for (XMLProperties p : listOfProperties) {
			this.buildNode(p);
		}
		this.writeToXmlDocument(destination);
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

	private void buildNode(XMLProperties xmlProperties) throws XPathExpressionException {

		Element elem = getDomElementById(xmlProperties);
		Map<String, String> map = extractValues(xmlProperties.getData(), ",");

		switch (elem.getTagName()) {
		case ELEMENT_TASK_USER:
			this.buildElementUserTask(xmlProperties, elem, map); // done
			break;
		case ELEMENT_GATEWAY_EXCLUSIVE:
			this.buildElementExclusiveGateway(xmlProperties, elem, map);
			break;
		case ELEMENT_GATEWAY_INCLUSIVE:
			this.buildElementInclusiveGateway(xmlProperties, elem, map);
			break;
		case ELEMENT_GATEWAY_PARALLEL:
			this.buildElementParallesGateway(xmlProperties, elem, map);
			break;
		case ELEMENT_SUBPROCESS:
			this.buildElementSubprocess(xmlProperties, elem, map);
			break;
		case ELEMENT_SEQUENCE_FLOW:
			this.buildElementSequenceFlow(xmlProperties, elem, map); // done
			break;
		case ELEMENT_EVENT_END:
			this.buildElementEndEvent(xmlProperties, elem, map); //done
			break;
		default:
			break;
		}
	}

	

	private void buildElementSubprocess(XMLProperties xmlProperties, Element elem, Map<String, String> map) {
		
		String sequantial = null;
		String collection = null;
		String elementVariable = null;
		
		Element em = (Element) doc.createElement(ELEMENT_MULTIINSTANCE);
				
		for (Entry<String, String> e : map.entrySet()) {
			
			switch (e.getKey()) {
			case ATTRIBUTE_MULTIINSTANCE_SEQUENTIAL:
				sequantial = e.getValue();
				break;
			case ATTRIBUTE_MULTIINSTANCE_COLLECTION:
				collection = e.getValue();
				break;
			case ATTRIBUTE_MULTIINSTANCE_VARIABLE:
				elementVariable = e.getValue();
				break;
			case ELEMENT_MULTIINSTANCE_CARDINALITY:
				Element lc = (Element) doc.createElement(ELEMENT_MULTIINSTANCE_CARDINALITY);
				lc.setTextContent(e.getValue());
				em.appendChild(lc);
				break;
			case ELEMENT_MULTIINSTANCE_CONDITION:
				Element cc = (Element) doc.createElement(ELEMENT_MULTIINSTANCE_CONDITION);
				cc.setTextContent(e.getValue());
				em.appendChild(cc);
				break;
			default:
				break;
			}
		}
		
		em.setAttribute(ATTRIBUTE_MULTIINSTANCE_SEQUENTIAL, sequantial);
		em.setAttribute(ACTIVITI_EXTENSIONS_PREFIX + COLON + ATTRIBUTE_MULTIINSTANCE_COLLECTION, collection);
		em.setAttribute(ACTIVITI_EXTENSIONS_PREFIX + COLON + ATTRIBUTE_MULTIINSTANCE_VARIABLE, elementVariable);
		
		elem.appendChild(em);
		
	}

	private void buildElementEndEvent(XMLProperties xmlProperties, Element elem, Map<String, String> map) {
		String data = null;
		for (Entry<String, String> e : map.entrySet()) {
			if (e.getKey().equalsIgnoreCase(ATTRIBUTE_ERROR_CODE))
				data = e.getValue();
		}
		
		if(data!=null){
			Element e = (Element)doc.createElement(ELEMENT_EVENT_ERRORDEFINITION);
			e.setAttribute(ATTRIBUTE_ERROR_REF, data);
			elem.appendChild(e);
		}
		
		
	}

	private void buildElementInclusiveGateway(XMLProperties xmlProperties, Element elem, Map<String, String> map) {
		// TODO Auto-generated method stub
		
	}

	private void buildElementExclusiveGateway(XMLProperties xmlProperties, Element elem, Map<String, String> map) {
		String data = null;
		for (Entry<String, String> e : map.entrySet()) {
			if (e.getKey().equalsIgnoreCase(ATTRIBUTE_DEFAULT))
				data = e.getValue();
		}
		
		if(data!=null){
			elem.setAttribute(ATTRIBUTE_DEFAULT, data);
		}
	}

	private void buildElementParallesGateway(XMLProperties xmlProperties, Element elem, Map<String, String> map) {
		// TODO Auto-generated method stub
		
	}

	private void buildElementUserTask(XMLProperties xmlProperties, Element elem, Map<String, String> map) {
		String assignee = null;
		String category = null;
		String candidates = null;

		for (Entry<String, String> e : map.entrySet()) {
			if (e.getKey().equalsIgnoreCase(ATTRIBUTE_TASK_USER_ASSIGNEE))
				assignee = e.getValue();
			else if (e.getKey().equalsIgnoreCase(ATTRIBUTE_TASK_USER_CATEGORY))
				category = e.getValue();
			else if (e.getKey().equalsIgnoreCase(ATTRIBUTE_TASK_USER_CANDIDATEUSERS))
				candidates = e.getValue();
		}
		if (!assignee.equals("null"))
			elem.setAttribute(ACTIVITI_EXTENSIONS_PREFIX + COLON + ATTRIBUTE_TASK_USER_ASSIGNEE, assignee);
		elem.setAttribute(ACTIVITI_EXTENSIONS_PREFIX + COLON + ATTRIBUTE_TASK_USER_CATEGORY, category);
		if (!candidates.equals("null"))
			elem.setAttribute(ACTIVITI_EXTENSIONS_PREFIX + COLON + ATTRIBUTE_TASK_USER_CANDIDATEUSERS, candidates);
	}

	private Element getDomElementById(XMLProperties xmlProperties) {
		XPath xPath = XPathFactory.newInstance().newXPath();
		char c[] = xmlProperties.getType().toCharArray();
		c[0] += 32;
		xmlProperties.setType(new String(c));
		String expression = "//" + xmlProperties.getType() + "[@id=" + "'" + xmlProperties.getId() + "'" + "]";
		try {
			return (Element) xPath.compile(expression).evaluate(doc, XPathConstants.NODE);
		} catch (XPathExpressionException ex) {
			ex.printStackTrace();
		}

		return null;
	}

	private void buildElementSequenceFlow(XMLProperties xmlProperties, Element elem, Map<String, String> map) {

		String data = null;
		for (Entry<String, String> e : map.entrySet()) {
			if (e.getKey().equalsIgnoreCase(FLOW_CONDITION))
				data = e.getValue();
		}

		if (!data.equals("null")) {
			Element e = (Element) doc.createElement(ELEMENT_FLOW_CONDITION);
			e.setAttribute(XSI_PREFIX + COLON + ATTRIBUTE_TYPE, "tFormalExpression");
			e.appendChild(doc.createCDATASection(data));
			Element oldChild = (Element) elem.getFirstChild();
			if (oldChild != null)
				elem.replaceChild(e, oldChild);
			else
				elem.appendChild(e);
		} else {
			Element oldChild = (Element) elem.getFirstChild();
			if (oldChild != null)
				elem.removeChild(oldChild);
		}
	}

	private static Map<String, String> extractValues(String data, String delim) {
		Map<String, String> m = new HashMap<String, String>();
		StringTokenizer tokenizer = new StringTokenizer(data, delim);
		while (tokenizer.hasMoreTokens()) {
			String[] part = tokenizer.nextToken().toString().split(":");
			m.put(part[0], part[1]);
		}
		return m;
	}

	private void writeToXmlDocument(String destination) throws SAXException, IOException {
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
