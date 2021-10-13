package org.lean.render.svg;

import org.junit.Test;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.util.TableFromHopPresentationUtil;

public class LeanPresentationWorkflowComponentTest extends LeanPresentationTestBase {
  
  @Test
  public void testPipelinesRender() throws Exception {
    LeanPresentation presentation = TableFromHopPresentationUtil.createWorkflowPresentation( 4000 );
    testRendering( presentation, "hop_workflow");
  }
}