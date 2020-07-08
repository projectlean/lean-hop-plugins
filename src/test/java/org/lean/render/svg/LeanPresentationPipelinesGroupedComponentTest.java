package org.lean.render.svg;

import org.junit.Test;
import org.lean.core.LeanAttachment;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.pipeline.LeanPipelineComponent;
import org.lean.presentation.component.types.composite.LeanCompositeComponent;
import org.lean.presentation.component.types.group.LeanGroupComponent;
import org.lean.presentation.component.types.label.LeanLabelComponent;
import org.lean.presentation.connector.LeanConnector;
import org.lean.presentation.connector.hop.LeanPipelineConnector;
import org.lean.presentation.layout.LeanLayout;
import org.lean.presentation.page.LeanPage;
import org.lean.presentation.util.BasePresentationUtil;

import java.util.Arrays;

public class LeanPresentationPipelinesGroupedComponentTest extends LeanPresentationTestBase {
  
  @Test
  public void testPipelinesGroupedComponentRender() throws Exception {

    LeanPresentation presentation = createGroupedPipelinePresentation( 3500 );
    testRendering( presentation, "hop_pipelines");
  }

  private LeanPresentation createGroupedPipelinePresentation( int nr ) throws Exception {

    LeanPresentation presentation = BasePresentationUtil.createBasePresentation(
      "Pipelines (" + nr + ")",
      "Pipelines " + nr + " description",
      100,
      "Renders a series of Hop pipelines",
      true
    );

    LeanPage pageOne = presentation.getPages().get( 0 );

    // The list of pipelines comes from the Hop connector running a pipeline itself
    //
    LeanPipelineConnector hopConnector = new LeanPipelineConnector(
      "src/test/resources/transformations/list-all-pipelines.hpl",
      "OUTPUT"
    );
    LeanConnector hop = new LeanConnector("Hop", hopConnector);
    presentation.getConnectors().add(hop);


    // We repeat a composite with a label and a pipeline rendering
    //
    LeanLabelComponent filenameLabelComponent = new LeanLabelComponent();
    filenameLabelComponent.setLabel( "Filename: ${short_filename}      Name: ${name}" );
    filenameLabelComponent.setThemeName( "Default" );
    LeanComponent filenameLabel = new LeanComponent("FilenameLabel", filenameLabelComponent);
    LeanLayout filenameLabelLayout = new LeanLayout();
    filenameLabelLayout.setLeft(new LeanAttachment(null, 0, 0, LeanAttachment.Alignment.TOP));
    filenameLabelLayout.setTop(new LeanAttachment(null, 0, 0, LeanAttachment.Alignment.LEFT));
    filenameLabel.setLayout( filenameLabelLayout );

    // Below this label is the pipeline
    //
    LeanPipelineComponent pipelineRenderingComponent = new LeanPipelineComponent( "${filename}" );
    LeanComponent pipelineRendering = new LeanComponent("Pipeline", pipelineRenderingComponent);
    LeanLayout pipelineRenderingLayout = new LeanLayout();
    pipelineRenderingLayout.setLeft( new LeanAttachment(null, 0, 0, LeanAttachment.Alignment.TOP) );
    pipelineRenderingLayout.setTop( new LeanAttachment("FilenameLabel", 0, 0, LeanAttachment.Alignment.BOTTOM) );
    pipelineRendering.setLayout( pipelineRenderingLayout );

    LeanCompositeComponent labelPipelineComponent = new LeanCompositeComponent( Arrays.asList(filenameLabel, pipelineRendering));
    LeanComponent labelPipeline = new LeanComponent("LabelPipeline", labelPipelineComponent);
    LeanLayout labelPipelineLayout = new LeanLayout();
    labelPipelineLayout.setLeft( new LeanAttachment(0, 0) );
    labelPipelineLayout.setRight( new LeanAttachment(100, 0) );
    labelPipelineLayout.setTop( new LeanAttachment(0, 0) );
    labelPipeline.setLayout( labelPipelineLayout );

    // For every output row of that pipeline we render a pipeline
    //
    LeanGroupComponent groupComponent = new LeanGroupComponent();
    groupComponent.setSourceConnectorName( "Hop" );
    groupComponent.setGroupComponent( labelPipeline );
    LeanComponent group = new LeanComponent("PipelineGroup", groupComponent);
    group.setLayout( new LeanLayout(new LeanAttachment(0, 0), new LeanAttachment(100, 0), new LeanAttachment(0, 0), null) );

    pageOne.getComponents().add(group);

    return presentation;
  }
}