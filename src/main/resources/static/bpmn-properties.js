$(document).ready(function() {

	//var diagramUrl = 'https://cdn.rawgit.com/bpmn-io/bpmn-js-examples/dfceecba/starter/diagram.bpmn';
	// modeler instance
	var bpmnModeler = new BpmnJS({
		container : '#canvas',
		keyboard : {
			bindTo : window
		}
	});
	/**
	 * Save diagram contents and print them to the console.
	 */


	function exportDiagram() {
		bpmnModeler.saveXML({
			format : true
		}, function(err, xml) {
			if (err) {
				return console.error('could not Render BPMN 2.0 diagram', err);
			}
			var __renderedXML = xml;

			var collection = {
				"xml" : xml,
				"properties" : objArray
			}

			var serializedData = JSON.stringify(collection);
			//console.log(serializedData);
			$.ajax({
				url : '/lms/bpmn/update',
				type : 'POST',
				contentType: "application/json",
				/*dataType : 'json',*/
				data : serializedData,
			})
				.done(function(data) {
					console.log("success");
				})
				.fail(function(jqXHR, exception) {
					var msg = '';
				    if (jqXHR.status === 0) {
				        msg = 'Not connect.\n Verify Network.';
				    } else if (jqXHR.status == 404) {
				        msg = 'Requested page not found. [404]';
				    } else if (jqXHR.status == 500) {
				        msg = 'Internal Server Error [500].';
				    } else if (exception === 'parsererror') {
				        msg = 'Requested JSON parse failed.';
				    } else if (exception === 'timeout') {
				        msg = 'Time out error.';
				    } else if (exception === 'abort') {
				        msg = 'Ajax request aborted.';
				    } else {
				        msg = 'Uncaught Error.\n' + jqXHR.responseText;
				    }
				    console.log(msg);
					
				})
				.always(function() {
					console.log("complete");
				});
		});
	}
	/**
	 * Open diagram in our modeler instance.
	 *
	 * @param {String} bpmnXML diagram to display
	 */
	function openDiagram(bpmnXML) {
		// import diagram
		bpmnModeler.importXML(bpmnXML, function(err) {
			if (err) {
				return console.error('could not import BPMN 2.0 diagram', err);
			}
			// access modeler components
			var canvas = bpmnModeler.get('canvas');
			var overlays = bpmnModeler.get('overlays');
			// zoom to fit full viewport
			canvas.zoom('fit-viewport');
			// attach an overlay to a node
			overlays.add('SCAN_OK', 'note', {
				position : {
					bottom : 0,
					right : 0
				},
				html : '<div class="diagram-note">Mixed up the labels?</div>'
			});
			// add marker
			canvas.addMarker('SCAN_OK', 'needs-discussion');
		});
	}
	// load external diagram file via AJAX and open it
	//$.get(diagramUrl, openDiagram, 'text');
	var initDiagram = '<?xml version="1.0" encoding="UTF-8"?>' +
		'<definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:xsi="http://www.w3.org/2001/' +
		'XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:activiti="http://activiti.org/bpmn" ' +
		'xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC"' +
		'xmlns:di="http://www.omg.org/spec/DD/20100524/DI" typeLanguage="http://www.w3.org/2001/XMLSchema" ' +
		'expressionLanguage="http://www.w3.org/1999/XPath" targetNamespace="http://www.activiti.org/test">' +
		'<process id="oneTaskProcess" name="the one task process" isExecutable="true">' +
		'<startEvent id="startEvent" name="Start"></startEvent>' +
		'<userTask id="Task1" name="firstStage" activiti:assignee="${person.id}"></userTask>' +
		'<sequenceFlow id="flow1" sourceRef="startEvent" targetRef="Task1"></sequenceFlow>' +
		'<exclusiveGateway id="exclusivegateway1" name="Exclusive Gateway"></exclusiveGateway>' +
		'<sequenceFlow id="flow2" sourceRef="Task1" targetRef="exclusivegateway1"></sequenceFlow>' +
		'<userTask id="task2" name="SecondStage"></userTask>' +
		'<sequenceFlow id="flow4" sourceRef="exclusivegateway1" targetRef="task2">' +
		'<conditionExpression xsi:type="tFormalExpression"><![CDATA[${approvedTaskA==true}]]></conditionExpression>' +
		'</sequenceFlow>' +
		'<exclusiveGateway id="exclusivegateway2" name="Exclusive Gateway"></exclusiveGateway>' +
		'<sequenceFlow id="flow5" sourceRef="task2" targetRef="exclusivegateway2"></sequenceFlow>' +
		'<endEvent id="endevent2" name="End"></endEvent>' +
		'<sequenceFlow id="flow6" sourceRef="exclusivegateway2" targetRef="endevent2">' +
		'<conditionExpression xsi:type="tFormalExpression"><![CDATA[${approvedTaskB==true}]]></conditionExpression>' +
		'</sequenceFlow>' +
		'<userTask id="usertask1" name="Third Stage"></userTask>' +
		'<sequenceFlow id="flow7" sourceRef="exclusivegateway2" targetRef="usertask1"></sequenceFlow>' +
		'<endEvent id="endevent3" name="End"></endEvent>' +
		'<sequenceFlow id="flow8" sourceRef="usertask1" targetRef="endevent3">' +

		'</sequenceFlow>' +
		'<serviceTask id="servicetask1" name="Service Task" activiti:expression="${write.writeMessage()}"></serviceTask>' +
		'<sequenceFlow id="flow9" sourceRef="exclusivegateway1" targetRef="servicetask1">' +
		'<conditionExpression xsi:type="tFormalExpression"><![CDATA[${approvedTaskA==false}]]></conditionExpression>' +
		'</sequenceFlow>' +
		'<endEvent id="endevent4" name="End"></endEvent>' +
		'<sequenceFlow id="flow10" sourceRef="servicetask1" targetRef="endevent4"></sequenceFlow>' +
		'</process>' +
		'<bpmndi:BPMNDiagram id="BPMNDiagram_oneTaskProcess">' +
		'<bpmndi:BPMNPlane bpmnElement="oneTaskProcess" id="BPMNPlane_oneTaskProcess">' +
		'<bpmndi:BPMNShape bpmnElement="startEvent" id="BPMNShape_startEvent">' +
		'<dc:Bounds height="35.0" width="35.0" x="50.0" y="190.0"></dc:Bounds>' +
		'</bpmndi:BPMNShape>' +
		'<bpmndi:BPMNShape bpmnElement="Task1" id="BPMNShape_Task1">' +
		'<dc:Bounds height="55.0" width="105.0" x="130.0" y="180.0"></dc:Bounds>' +
		'</bpmndi:BPMNShape>' +
		'<bpmndi:BPMNShape bpmnElement="exclusivegateway1" id="BPMNShape_exclusivegateway1">' +
		'<dc:Bounds height="40.0" width="40.0" x="280.0" y="188.0"></dc:Bounds>' +
		'</bpmndi:BPMNShape>' +
		'<bpmndi:BPMNShape bpmnElement="task2" id="BPMNShape_task2">' +
		'<dc:Bounds height="55.0" width="105.0" x="365.0" y="300.0"></dc:Bounds>' +
		'</bpmndi:BPMNShape>' +
		'<bpmndi:BPMNShape bpmnElement="exclusivegateway2" id="BPMNShape_exclusivegateway2">' +
		'<dc:Bounds height="40.0" width="40.0" x="515.0" y="308.0"></dc:Bounds>' +
		'</bpmndi:BPMNShape>' +
		'<bpmndi:BPMNShape bpmnElement="endevent2" id="BPMNShape_endevent2">' +
		'<dc:Bounds height="35.0" width="35.0" x="600.0" y="311.0"></dc:Bounds>' +
		'</bpmndi:BPMNShape>' +
		'<bpmndi:BPMNShape bpmnElement="usertask1" id="BPMNShape_usertask1">' +
		'<dc:Bounds height="55.0" width="105.0" x="590.0" y="420.0"></dc:Bounds>' +
		'</bpmndi:BPMNShape>' +
		'<bpmndi:BPMNShape bpmnElement="endevent3" id="BPMNShape_endevent3">' +
		'<dc:Bounds height="35.0" width="35.0" x="740.0" y="430.0"></dc:Bounds>' +
		'</bpmndi:BPMNShape>' +
		'<bpmndi:BPMNShape bpmnElement="servicetask1" id="BPMNShape_servicetask1">' +
		'<dc:Bounds height="55.0" width="105.0" x="460.0" y="180.0"></dc:Bounds>' +
		'</bpmndi:BPMNShape>' +
		'<bpmndi:BPMNShape bpmnElement="endevent4" id="BPMNShape_endevent4">' +
		'<dc:Bounds height="35.0" width="35.0" x="610.0" y="190.0"></dc:Bounds>' +
		'</bpmndi:BPMNShape>' +
		'<bpmndi:BPMNEdge bpmnElement="flow1" id="BPMNEdge_flow1">' +
		'<di:waypoint x="85.0" y="207.0"></di:waypoint>' +
		'<di:waypoint x="130.0" y="207.0"></di:waypoint>' +
		'</bpmndi:BPMNEdge>' +
		'<bpmndi:BPMNEdge bpmnElement="flow2" id="BPMNEdge_flow2">' +
		'<di:waypoint x="235.0" y="207.0"></di:waypoint>' +
		'<di:waypoint x="280.0" y="208.0"></di:waypoint>' +
		'</bpmndi:BPMNEdge>' +
		'<bpmndi:BPMNEdge bpmnElement="flow4" id="BPMNEdge_flow4">' +
		'<di:waypoint x="300.0" y="228.0"></di:waypoint>' +
		'<di:waypoint x="300.0" y="327.0"></di:waypoint>' +
		'<di:waypoint x="365.0" y="327.0"></di:waypoint>' +
		'</bpmndi:BPMNEdge>' +
		'<bpmndi:BPMNEdge bpmnElement="flow5" id="BPMNEdge_flow5">' +
		'<di:waypoint x="470.0" y="327.0"></di:waypoint>' +
		'<di:waypoint x="515.0" y="328.0"></di:waypoint>' +
		'</bpmndi:BPMNEdge>' +
		'<bpmndi:BPMNEdge bpmnElement="flow6" id="BPMNEdge_flow6">' +
		'<di:waypoint x="555.0" y="328.0"></di:waypoint>' +
		'<di:waypoint x="600.0" y="328.0"></di:waypoint>' +
		'</bpmndi:BPMNEdge>' +
		'<bpmndi:BPMNEdge bpmnElement="flow7" id="BPMNEdge_flow7">' +
		'<di:waypoint x="535.0" y="348.0"></di:waypoint>' +
		'<di:waypoint x="535.0" y="447.0"></di:waypoint>' +
		'<di:waypoint x="590.0" y="447.0"></di:waypoint>' +
		'</bpmndi:BPMNEdge>' +
		'<bpmndi:BPMNEdge bpmnElement="flow8" id="BPMNEdge_flow8">' +
		'<di:waypoint x="695.0" y="447.0"></di:waypoint>' +
		'<di:waypoint x="740.0" y="447.0"></di:waypoint>' +
		'</bpmndi:BPMNEdge>' +
		'<bpmndi:BPMNEdge bpmnElement="flow9" id="BPMNEdge_flow9">' +
		'<di:waypoint x="320.0" y="208.0"></di:waypoint>' +
		'<di:waypoint x="460.0" y="207.0"></di:waypoint>' +
		'</bpmndi:BPMNEdge>' +
		'<bpmndi:BPMNEdge bpmnElement="flow10" id="BPMNEdge_flow10">' +
		'<di:waypoint x="565.0" y="207.0"></di:waypoint>' +
		'<di:waypoint x="610.0" y="207.0"></di:waypoint>' +
		'</bpmndi:BPMNEdge>' +
		'</bpmndi:BPMNPlane>' +
		'</bpmndi:BPMNDiagram>' +
		'</definitions>';
	openDiagram(initDiagram);

	$('.bjs-powered-by').remove();

	$('#zoom-in-canvas').click(function(event) {
		event.preventDefault();
		var zoom = bpmnModeler.get('zoomScroll');
		zoom.stepZoom(1);
	});

	$('#zoom-out-canvas').click(function(event) {
		event.preventDefault();
		console.log("working");
		var zoom = bpmnModeler.get('zoomScroll');
		zoom.stepZoom(-1);
	});

	$('#reset-canvas').click(function(event) {
		event.preventDefault();
		var canvas = bpmnModeler.get('canvas');
		canvas.zoom('fit-viewport');
	});

	// wire save button
	$('#compile-model').click(exportDiagram);
});