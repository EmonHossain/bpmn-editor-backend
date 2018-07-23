package com.bpmneditor.core;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/lms/bpmn")
public class Bpmn {
	
	@GetMapping("/editor")
	public String bpmnEditor(){
		return "modeler";
	}
	
	@PostMapping(value="/update")
	public ResponseEntity<String> bpmnModifier(@RequestBody BpmnData bpmn,BindingResult result){
		if(result.hasErrors()){
			return new ResponseEntity<String>("",HttpStatus.BAD_REQUEST);
		}else
			System.out.println(bpmn.getProperties());
		return new ResponseEntity<String>("",HttpStatus.OK);
	}
}
