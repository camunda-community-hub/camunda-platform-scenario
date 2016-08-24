package org.camunda.bpm.scenario.delegate;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.task.DelegationState;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.scenario.runner.ScenarioRunnerImpl;
import org.camunda.bpm.scenario.runner.Waitstate;

import java.util.Date;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class TaskDelegate extends Waitstate<Task> implements Task {

  public TaskDelegate(ScenarioRunnerImpl runner, HistoricActivityInstance instance, String duration) {
    super(runner, instance, duration);
  }

  public String getId() {
    return runtimeDelegate.getId();
  }

  public String getName() {
    return runtimeDelegate.getName();
  }

  public void setName(String name) {
    runtimeDelegate.setName(name);
  }

  public String getDescription() {
    return runtimeDelegate.getDescription();
  }

  public void setDescription(String description) {
    runtimeDelegate.setDescription(description);
  }

  public int getPriority() {
    return runtimeDelegate.getPriority();
  }

  public void setPriority(int priority) {
    runtimeDelegate.setPriority(priority);
  }

  public String getOwner() {
    return runtimeDelegate.getOwner();
  }

  public void setOwner(String owner) {
    runtimeDelegate.setOwner(owner);
  }

  public String getAssignee() {
    return runtimeDelegate.getAssignee();
  }

  public void setAssignee(String assignee) {
    runtimeDelegate.setAssignee(assignee);
  }

  public DelegationState getDelegationState() {
    return runtimeDelegate.getDelegationState();
  }

  public void setDelegationState(DelegationState delegationState) {
    runtimeDelegate.setDelegationState(delegationState);
  }

  public String getProcessInstanceId() {
    return runtimeDelegate.getProcessInstanceId();
  }

  public String getProcessDefinitionId() {
    return runtimeDelegate.getProcessDefinitionId();
  }

  public String getCaseInstanceId() {
    return runtimeDelegate.getCaseInstanceId();
  }

  public void setCaseInstanceId(String caseInstanceId) {
    runtimeDelegate.setCaseInstanceId(caseInstanceId);
  }

  public String getCaseExecutionId() {
    return runtimeDelegate.getCaseExecutionId();
  }

  public String getCaseDefinitionId() {
    return runtimeDelegate.getCaseDefinitionId();
  }

  public Date getCreateTime() {
    return runtimeDelegate.getCreateTime();
  }

  public String getTaskDefinitionKey() {
    return runtimeDelegate.getTaskDefinitionKey();
  }

  public Date getDueDate() {
    return runtimeDelegate.getDueDate();
  }

  public void setDueDate(Date dueDate) {
    runtimeDelegate.setDueDate(dueDate);
  }

  public Date getFollowUpDate() {
    return runtimeDelegate.getFollowUpDate();
  }

  public void setFollowUpDate(Date dueDate) {
    runtimeDelegate.setFollowUpDate(dueDate);
  }

  public void delegate(String userId) {
    runtimeDelegate.delegate(userId);
  }

  public void setParentTaskId(String parentTaskId) {
    runtimeDelegate.setParentTaskId(parentTaskId);
  }

  public String getParentTaskId() {
    return runtimeDelegate.getParentTaskId();
  }

  public boolean isSuspended() {
    return runtimeDelegate.isSuspended();
  }

  public String getFormKey() {
    return runtimeDelegate.getFormKey();
  }

  public String getTenantId() {
    return runtimeDelegate.getTenantId();
  }

  public void setTenantId(String tenantId) {
    runtimeDelegate.setTenantId(tenantId);
  }

}
