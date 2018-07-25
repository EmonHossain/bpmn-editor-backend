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

	//private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	public void prepareXml(String xml, List<XMLProperties> listOfProperties,String destination) throws IOException, SAXException, XPathExpressionException {
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
		XPath xPath = XPathFactory.newInstance().newXPath();
		char c[] = xmlProperties.getType().toCharArray();
		c[0] += 32;
		xmlProperties.setType(new String(c));
		String expression = "//"+xmlProperties.getType()+"[@id=" + "'" + xmlProperties.getId() + "'" + "]";
		Element elem = (Element) xPath.compile(expression).evaluate(doc, XPathConstants.NODE);
		Map<String, String> map = extractValues(xmlProperties.getData(), ",");
		
		switch (elem.getTagName()) {
		case ELEMENT_TASK_USER:
			String assignee = null;
			String category = null;
			String candidates = null;
			for(Entry<String, String> e : map.entrySet()){
				if(e.getKey().equalsIgnoreCase(ATTRIBUTE_TASK_USER_ASSIGNEE))
					assignee = e.getValue();
				else if(e.getKey().equalsIgnoreCase(ATTRIBUTE_TASK_USER_CATEGORY))
					category = e.getValue();
				else if(e.getKey().equalsIgnoreCase(ATTRIBUTE_TASK_USER_CANDIDATEUSERS))
					candidates = e.getValue();
			}
			if(!assignee.equals("null"))
				elem.setAttribute(ACTIVITI_EXTENSIONS_PREFIX + COLON + ATTRIBUTE_TASK_USER_ASSIGNEE,assignee);
			elem.setAttribute(ACTIVITI_EXTENSIONS_PREFIX + COLON + ATTRIBUTE_TASK_USER_CATEGORY, category);
			if(!candidates.equals("null"))
				elem.setAttribute(ACTIVITI_EXTENSIONS_PREFIX + COLON + ATTRIBUTE_TASK_USER_CANDIDATEUSERS, candidates);
			break;
		case ELEMENT_GATEWAY_EXCLUSIVE:
			break;
		case ELEMENT_GATEWAY_INCLUSIVE:
			break;
		case ELEMENT_GATEWAY_PARALLEL:
			break;
		case ELEMENT_SEQUENCE_FLOW:
			String data = null;
			for(Entry<String, String> e : map.entrySet()){
				if(e.getKey().equalsIgnoreCase(FLOW_CONDITION))
						data = e.getValue();
			}
			
			if(!data.equals("null")){
				Element e = (Element) doc.createElement(ELEMENT_FLOW_CONDITION);
				e.setAttribute(XSI_PREFIX + COLON + ATTRIBUTE_TYPE, "tFormalExpression");
				e.appendChild(doc.createCDATASection(data));
				Element oldChild = (Element) elem.getFirstChild();
				if(oldChild!=null)
					elem.replaceChild(e, oldChild);
				else
					elem.appendChild(e);
			}else{
				Element oldChild = (Element) elem.getFirstChild();
				if(oldChild!=null)
					elem.removeChild(oldChild);
			}
			
			break;
		default:
			break;
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
