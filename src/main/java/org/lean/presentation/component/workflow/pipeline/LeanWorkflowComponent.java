package org.lean.presentation.component.workflow.pipeline;

import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.gui.AreaOwner;
import org.apache.hop.core.gui.Point;
import org.apache.hop.core.gui.SvgGc;
import org.apache.hop.core.svg.HopSvgGraphics2D;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.PipelinePainter;
import org.apache.hop.workflow.WorkflowMeta;
import org.apache.hop.workflow.WorkflowPainter;
import org.lean.core.LeanGeometry;
import org.lean.core.LeanSize;
import org.lean.core.exception.LeanException;
import org.lean.presentation.LeanComponentLayoutResult;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.type.ILeanComponent;
import org.lean.presentation.component.type.LeanBaseComponent;
import org.lean.presentation.component.type.LeanComponentPlugin;
import org.lean.presentation.datacontext.IDataContext;
import org.lean.presentation.layout.LeanLayoutResults;
import org.lean.presentation.layout.LeanRenderPage;
import org.lean.presentation.page.LeanPage;
import org.lean.render.IRenderContext;

import java.util.ArrayList;
import java.util.List;

/**
 * A component to render a workflow in SVG
 */
@LeanComponentPlugin(
  id = "LeanWorkflowComponent",
  name = "Hop Workflow",
  description = "A component to render a workflow in SVG"
)
public class LeanWorkflowComponent extends LeanBaseComponent implements ILeanComponent {

  private static final String DATA_WORKFLOW_DETAILS = "Workflow details";

  @HopMetadataProperty
  private String filename;

  public LeanWorkflowComponent( String filename ) {
    super( "LeanWorkflowComponent" );
    this.filename = filename;
  }

  public LeanWorkflowComponent( LeanWorkflowComponent c ) {
    super( "LeanWorkflowComponent", c );
    this.filename = c.filename;
  }

  public LeanWorkflowComponent clone() {
    return new LeanWorkflowComponent( this );
  }

  public void processSourceData( LeanPresentation presentation, LeanPage page, LeanComponent component, IDataContext dataContext, IRenderContext renderContext, LeanLayoutResults results )
    throws LeanException {
    if ( StringUtils.isEmpty( filename ) ) {
      throw new LeanException( "No pipeline filename specified" );
    }

    WorkflowDetails details = new WorkflowDetails();
    try {
      details.workflowMeta = new WorkflowMeta( dataContext.getVariableSpace(), filename, dataContext.getMetadataProvider() );
      details.maximum = details.workflowMeta.getMaximum();
      details.minimum = details.workflowMeta.getMinimum();
      details.size = new LeanSize( details.maximum.x - details.minimum.x, details.maximum.y - details.minimum.y );

      results.addDataSet( component, DATA_WORKFLOW_DETAILS, details );
      Point min = details.workflowMeta.getMinimum();

    } catch ( Exception e ) {
      throw new LeanException( "Error loading workflow from filename " + filename, e );
    }
  }

  public LeanSize getExpectedSize( LeanPresentation presentation, LeanPage page, LeanComponent component, IDataContext dataContext, IRenderContext renderContext, LeanLayoutResults results )
    throws LeanException {

    WorkflowDetails details = (WorkflowDetails) results.getDataSet( component, DATA_WORKFLOW_DETAILS );
    return details.size;
  }

  public void render( LeanComponentLayoutResult layoutResult, LeanLayoutResults results, IRenderContext renderContext ) throws LeanException {
    try {
      LeanRenderPage renderPage = layoutResult.getRenderPage();
      LeanGeometry componentGeometry = layoutResult.getGeometry();
      LeanComponent component = layoutResult.getComponent();

      WorkflowDetails details = (WorkflowDetails) results.getDataSet( component, DATA_WORKFLOW_DETAILS );

      HopSvgGraphics2D gc = layoutResult.getRenderPage().getGc();

      // Draw onto a separate document, then embed the SVG into the parent
      //
      HopSvgGraphics2D workflowGc = HopSvgGraphics2D.newDocument();

      List<AreaOwner> areaOwners = new ArrayList<AreaOwner>();
      SvgGc svgGc = new SvgGc( workflowGc, new Point( details.size.getWidth(), details.size.getHeight() ), 32, 0, 0 );

      // Maybe we can scroll a bit to the right?
      //
      WorkflowPainter workflowPainter = new WorkflowPainter( svgGc, details.workflowMeta,
        new Point( details.size.getWidth(), details.size.getHeight() ), null, null, null, null, null,
        areaOwners, 32, 1, 0, "Arial", 10, 1.0d );
      workflowPainter.setMagnification( 1.0f );
      workflowPainter.setOffset( new Point(-details.minimum.x, -details.minimum.y) );
      workflowPainter.drawWorkflow();

      // Zoom in or out to make the image fit into the component geometry
      //
      float xMagnification = (float) componentGeometry.getWidth() / (float) details.size.getWidth();
      float yMagnification = (float) componentGeometry.getHeight() / (float) details.size.getHeight();
      float magnification = Math.min( xMagnification, yMagnification );

      gc.embedSvg(
        workflowGc.getRoot(),
        filename,
        componentGeometry.getX(),
        componentGeometry.getY(),
        details.size.getWidth(),
        details.size.getHeight(),
        magnification,
        magnification,
        0
      );

      if ( isBorder() ) {
        enableColor( gc, lookupBorderColor( renderContext ) );
        gc.drawRect( componentGeometry.getX(), componentGeometry.getY(), details.size.getWidth(), details.size.getHeight() );
      }

      // add drawnItem for this
      //
      renderPage.addComponentDrawnItem( component, componentGeometry );

    } catch ( Exception e ) {
      throw new LeanException( "Error rendering workflow from file " + filename, e );
    }
  }
}
