package org.lean.render.svg;

import org.junit.Ignore;
import org.junit.Test;
import org.lean.core.LeanAttachment;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.pipeline.LeanPipelineComponent;
import org.lean.presentation.component.types.composite.LeanCompositeComponent;
import org.lean.presentation.component.types.group.LeanGroupComponent;
import org.lean.presentation.component.types.label.LeanLabelComponent;
import org.lean.presentation.connector.LeanConnector;
import org.lean.presentation.connector.hop.LeanHopConnector;
import org.lean.presentation.layout.LeanLayout;
import org.lean.presentation.page.LeanPage;
import org.lean.presentation.util.BasePresentationUtil;
import org.lean.presentation.util.TableFromHopPresentationUtil;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

public class LeanPresentationPipelineComponentTest extends LeanPresentationTestBase {
  
  @Test
  public void testPipelinesRender() throws Exception {
    LeanPresentation presentation = TableFromHopPresentationUtil.createPipelinePresentation( 3000 );
    testRendering( presentation, "hop_pipeline");
  }
}