package org.camunda.bpm.scenario.impl.delegate;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.task.DelegationState;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.scenario.delegate.TaskDelegate;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;
import org.camunda.bpm.scenario.impl.WaitstateExecutable;

import java.util.Date;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class AbstractTaskDelegate extends WaitstateExecutable<Task> implements TaskDelegate {

  public AbstractTaskDelegate(ProcessRunnerImpl runner, HistoricActivityInstance instance) {
    super(runner, instance);
  }

  public String getId() {
    return delegate.getId();
  }

  public String getName() {
    return delegate.getName();
  }

  public void setName(String name) {
    delegate.setName(name);
  }

  public String getDescription() {
    return delegate.getDescription();
  }

  public void setDescription(String description) {
    delegate.setDescription(description);
  }

  public int getPriority() {
    return delegate.getPriority();
  }

  public void setPriority(int priority) {
    delegate.setPriority(priority);
  }

  public String getOwner() {
    return delegate.getOwner();
  }

  public void setOwner(String owner) {
    delegate.setOwner(owner);
  }

  public String getAssignee() {
    return delegate.getAssignee();
  }

  public void setAssignee(String assignee) {
    delegate.setAssignee(assignee);
  }

  public DelegationState getDelegationState() {
    return delegate.getDelegationState();
  }

  public void setDelegationState(DelegationState delegationState) {
    delegate.setDelegationState(delegationState);
  }

  public String getProcessInstanceId() {
    return delegate.getProcessInstanceId();
  }

  public String getProcessDefinitionId() {
    return delegate.getProcessDefinitionId();
  }

  public String getCaseInstanceId() {
    return delegate.getCaseInstanceId();
  }

  public void setCaseInstanceId(String caseInstanceId) {
    delegate.setCaseInstanceId(caseInstanceId);
  }

  public String getCaseExecutionId() {
    return delegate.getCaseExecutionId();
  }

  public String getCaseDefinitionId() {
    return delegate.getCaseDefinitionId();
  }

  public Date getCreateTime() {
    return delegate.getCreateTime();
  }

  public String getTaskDefinitionKey() {
    return delegate.getTaskDefinitionKey();
  }

  public Date getDueDate() {
    return delegate.getDueDate();
  }

  public void setDueDate(Date dueDate) {
    delegate.setDueDate(dueDate);
  }

  public Date getFollowUpDate() {
    return delegate.getFollowUpDate();
  }

  public void setFollowUpDate(Date dueDate) {
    delegate.setFollowUpDate(dueDate);
  }

  public void delegate(String userId) {
    delegate.delegate(userId);
  }

  public void setParentTaskId(String parentTaskId) {
    delegate.setParentTaskId(parentTaskId);
  }

  public String getParentTaskId() {
    return delegate.getParentTaskId();
  }

  public boolean isSuspended() {
    return delegate.isSuspended();
  }

  public String getFormKey() {
    return delegate.getFormKey();
  }

  public String getTenantId() {
    return delegate.getTenantId();
  }

  public void setTenantId(String tenantId) {
    delegate.setTenantId(tenantId);
  }

}
