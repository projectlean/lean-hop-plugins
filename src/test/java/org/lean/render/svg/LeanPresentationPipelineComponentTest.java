package org.lean.render.svg;

import org.junit.Test;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.util.TableFromHopPresentationUtil;

import static org.junit.Assert.assertNotNull;

public class LeanPresentationPipelineComponentTest extends LeanPresentationTestBase {
  
  @Test
  public void testPipelinesRender() throws Exception {
    LeanPresentation presentation = TableFromHopPresentationUtil.createPipelinePresentation( 3000 );
    testRendering( presentation, "hop_pipeline");
  }
}