package org.lean.presentation.component.workflow.pipeline;

import org.apache.hop.core.gui.Point;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.workflow.WorkflowMeta;
import org.lean.core.LeanSize;

public class WorkflowDetails {
  public WorkflowMeta workflowMeta;
  public LeanSize size;
  public Point maximum;
  public Point minimum;
  public IVariables variables;
  public float magnification;
}
