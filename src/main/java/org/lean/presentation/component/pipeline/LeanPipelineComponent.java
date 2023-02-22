package org.lean.presentation.component.pipeline;

import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.gui.AreaOwner;
import org.apache.hop.core.gui.DPoint;
import org.apache.hop.core.gui.Point;
import org.apache.hop.core.gui.SvgGc;
import org.apache.hop.core.svg.HopSvgGraphics2D;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.PipelinePainter;
import org.lean.core.LeanGeometry;
import org.lean.core.LeanPosition;
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
import java.util.HashMap;
import java.util.List;

/** A component to render a pipeline in SVG */
@LeanComponentPlugin(
    id = "LeanWorkflowComponent",
    name = "Hop Pipeline",
    description = "A component to render a pipeline in SVG")
public class LeanPipelineComponent extends LeanBaseComponent implements ILeanComponent {

  private static final String DATA_PIPELINE_DETAILS = "Pipeline details";

  @HopMetadataProperty private String filename;

  public LeanPipelineComponent(String filename) {
    super("LeanWorkflowComponent");
    this.filename = filename;
  }

  public LeanPipelineComponent(LeanPipelineComponent c) {
    super("LeanWorkflowComponent", c);
    this.filename = c.filename;
  }

  public LeanPipelineComponent clone() {
    return new LeanPipelineComponent(this);
  }

  public void processSourceData(
      LeanPresentation presentation,
      LeanPage page,
      LeanComponent component,
      IDataContext dataContext,
      IRenderContext renderContext,
      LeanLayoutResults results)
      throws LeanException {
    if (StringUtils.isEmpty(filename)) {
      throw new LeanException("No pipeline filename specified");
    }

    PipelineDetails details = new PipelineDetails();
    String realFilename = dataContext.getVariables().resolve(filename);
    try {
      details.pipelineMeta =
          new PipelineMeta(
              realFilename, dataContext.getMetadataProvider(), dataContext.getVariables());
      details.maximum = details.pipelineMeta.getMaximum();
      details.minimum = details.pipelineMeta.getMinimum();
      details.size =
          new LeanSize(
              details.maximum.x - details.minimum.x, details.maximum.y - details.minimum.y);
      details.variables = dataContext.getVariables();

      // Zoom in or out to make the image fit onto the parent page (it's the best use-case for now)
      //
      float xMagnification =
          Math.min(1.0f, (float) page.getWidth() / (float) details.size.getWidth());
      float yMagnification =
          Math.min(1.0f, (float) page.getHeight() / (float) details.size.getHeight());
      details.magnification = Math.min(xMagnification, yMagnification) * 0.9f;

      // Now we can re-size
      //
      details.size =
          new LeanSize(
              (int) (details.size.getWidth() * details.magnification),
              (int) (details.size.getHeight() * details.magnification));

      results.addDataSet(component, DATA_PIPELINE_DETAILS, details);

    } catch (Exception e) {
      throw new LeanException("Error loading pipeline from filename " + realFilename, e);
    }
  }

  public LeanSize getExpectedSize(
      LeanPresentation presentation,
      LeanPage page,
      LeanComponent component,
      IDataContext dataContext,
      IRenderContext renderContext,
      LeanLayoutResults results)
      throws LeanException {

    PipelineDetails details =
        (PipelineDetails) results.getDataSet(component, DATA_PIPELINE_DETAILS);
    return details.size;
  }

  public void render(
      LeanComponentLayoutResult layoutResult,
      LeanLayoutResults results,
      IRenderContext renderContext,
      LeanPosition leanPosition)
      throws LeanException {
    try {
      LeanRenderPage renderPage = layoutResult.getRenderPage();
      LeanGeometry componentGeometry = layoutResult.getGeometry();
      LeanComponent component = layoutResult.getComponent();

      PipelineDetails details =
          (PipelineDetails) results.getDataSet(component, DATA_PIPELINE_DETAILS);

      HopSvgGraphics2D gc = renderPage.getGc();

      // Draw onto a separate document, then embed the SVG into the parent
      //
      HopSvgGraphics2D pipelineGc = HopSvgGraphics2D.newDocument();

      List<AreaOwner> areaOwners = new ArrayList<>();
      SvgGc svgGc =
          new SvgGc(
              pipelineGc, new Point(details.size.getWidth(), details.size.getHeight()), 32, 0, 0);

      // Maybe we can scroll a bit to the right?
      //
      PipelinePainter pipelinePainter =
          new PipelinePainter(
              svgGc,
              details.variables,
              details.pipelineMeta,
              new Point(details.size.getWidth(), details.size.getHeight()),
              null,
              null,
              null,
              areaOwners,
              32,
              1,
              0,
              "Arial",
              10,
              1.0d,
              false,
              new HashMap<>());
      pipelinePainter.setMagnification(1.0f);
      pipelinePainter.setOffset(new DPoint(-details.minimum.x, -details.minimum.y));
      pipelinePainter.drawPipelineImage();

      // String pipelineSvgXml = pipelineGc.toXml(); // Not needed

      gc.embedSvg(
          pipelineGc.getRoot(),
          filename,
          componentGeometry.getX(),
          componentGeometry.getY(),
          details.size.getWidth(),
          details.size.getHeight(),
          details.magnification,
          details.magnification,
          0);

    } catch (Exception e) {
      throw new LeanException("Error rendering pipeline from file " + filename, e);
    }
  }
}
