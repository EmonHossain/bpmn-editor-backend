package com.bpmneditor.core;

import java.io.IOException;

import javax.xml.xpath.XPathExpressionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.xml.sax.SAXException;

@Controller
@RequestMapping("/lms/bpmn")
public class Bpmn {
	
	@Value("${activiti-temp-resource}")
	private String DES;
	
	@Autowired
	private XMLWriterDom dom;
	
	@GetMapping("/editor")
	public String bpmnEditor(){
		return "modeler";
	}
	
	@PostMapping(value="/update")
	public ResponseEntity<String> bpmnModifier(@RequestBody BpmnData bpmn,BindingResult result) throws IOException, SAXException, XPathExpressionException{
		if(result.hasErrors()){
			return new ResponseEntity<String>("",HttpStatus.BAD_REQUEST);
		}else{
			System.out.println(bpmn.getProperties());
			dom.prepareXml(bpmn.getXml(), bpmn.getProperties(), DES+"test.bpmn20.xml");
		}
		return new ResponseEntity<String>("",HttpStatus.OK);
	}
}
