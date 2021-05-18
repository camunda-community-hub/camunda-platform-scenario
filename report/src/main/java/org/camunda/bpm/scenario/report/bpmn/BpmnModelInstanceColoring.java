package org.camunda.bpm.scenario.report.bpmn;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnDiagram;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Martin Schimak
 */
enum BpmnModelInstanceColoring {

  COMPLETED_INNER("rgb(200, 230, 201)"), COMPLETED_OUTER("rgb(67, 160, 71)"),
  ACTIVE_INNER("rgb(187, 222, 251)"), ACTIVE_OUTER("rgb(30, 136, 229)"),
  CANCELED_INNER("lightgrey"), CANCELED_OUTER("darkgrey"),
  UNTOUCHED_INNER("white"), UNTOUCHED_OUTER("black");

  private static final String NAMESPACE = "http://bpmn.io/schema/bpmn/biocolor/1.0";
  private static final String PREFIX = "bioc";

  private final String code;

  BpmnModelInstanceColoring(String code) {
    this.code = code;
  }

  public static BpmnModelInstance color(BpmnModelInstance bpmnModelInstance, List<HistoricActivityInstance> activities) {

    String processDefinitionKey = activities.get(0).getProcessDefinitionKey();
    bpmnModelInstance.getDocument().registerNamespace(PREFIX, NAMESPACE);
    BpmnDiagram bpmnDiagram = bpmnModelInstance.getDefinitions().getBpmDiagrams().iterator().next();
    Collection<BpmnShape> bpmnShapes = bpmnDiagram.getBpmnPlane().getChildElementsByType(BpmnShape.class)
      .stream().filter(shape -> {
        ModelElementInstance element = shape.getBpmnElement();
        while (!(element == null || element instanceof Process))
          element = element.getParentElement();
        return element != null && ((Process) element).getId().equals(processDefinitionKey);
      }).collect(Collectors.toList());
    bpmnShapes.forEach(bpmnShape -> color(bpmnShape, activities));
    return bpmnModelInstance;

  }

  private static BpmnShape color(BpmnShape bpmnShape, List<HistoricActivityInstance> activities) {

    String activityId = bpmnShape.getAttributeValue("bpmnElement");

    List<HistoricActivityInstance> relatedActivities = activities.stream()
      .filter(a -> a.getActivityId().equals(activityId)).collect(Collectors.toList());

    BpmnModelInstanceColoring outer = BpmnModelInstanceColoring.UNTOUCHED_OUTER;
    BpmnModelInstanceColoring inner = BpmnModelInstanceColoring.UNTOUCHED_INNER;

    if (relatedActivities.stream().findFirst().isPresent()) {
      if (relatedActivities.stream().anyMatch(a -> !a.isCanceled() && a.getEndTime() != null)) {
        outer = BpmnModelInstanceColoring.COMPLETED_OUTER;
        inner = BpmnModelInstanceColoring.COMPLETED_INNER;
      } else if (relatedActivities.stream().anyMatch(a -> a.isCanceled())) {
        outer = BpmnModelInstanceColoring.CANCELED_OUTER;
        inner = BpmnModelInstanceColoring.CANCELED_INNER;
      } else {
        outer = BpmnModelInstanceColoring.ACTIVE_OUTER;
        inner = BpmnModelInstanceColoring.ACTIVE_INNER;
      }
    }

    bpmnShape.setAttributeValueNs(BpmnModelInstanceColoring.NAMESPACE, "stroke", outer.code);
    bpmnShape.setAttributeValueNs(BpmnModelInstanceColoring.NAMESPACE, "fill", inner.code);

    return bpmnShape;

  }

}
