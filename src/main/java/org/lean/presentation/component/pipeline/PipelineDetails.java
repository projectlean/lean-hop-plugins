package org.lean.presentation.component.pipeline;

import org.apache.hop.core.gui.Point;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.pipeline.PipelineMeta;
import org.lean.core.LeanSize;

public class PipelineDetails {
  public PipelineMeta pipelineMeta;
  public IVariables variables;
  public LeanSize size;
  public Point maximum;
  public Point minimum;
  public float magnification;
}
