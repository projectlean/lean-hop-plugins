package org.lean.render.svg;

import org.apache.hop.core.annotations.Transform;
import org.apache.hop.core.logging.ILoggingObject;
import org.apache.hop.core.logging.LoggingObject;
import org.apache.hop.core.plugins.PluginRegistry;
import org.apache.hop.core.plugins.TransformPluginType;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.metadata.serializer.memory.MemoryMetadataProvider;
import org.apache.hop.pipeline.transforms.csvinput.CsvInputMeta;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.lean.core.LeanEnvironment;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.datacontext.IDataContext;
import org.lean.presentation.datacontext.PresentationDataContext;
import org.lean.presentation.layout.LeanLayoutResults;
import org.lean.presentation.layout.LeanRenderPage;
import org.lean.render.IRenderContext;
import org.lean.render.context.SimpleRenderContext;

import java.io.File;

import static org.junit.Assert.assertNotNull;


@Ignore
public class LeanPresentationTestBase {

  protected IHopMetadataProvider metadataProvider;
  protected ILoggingObject parent;
  protected String folderName;

  @Before
  public void setUp() throws Exception {

    // Create a metastore
    //
    metadataProvider = new MemoryMetadataProvider();
    LeanEnvironment.init();

    PluginRegistry.getInstance().registerPluginClass( CsvInputMeta.class.getName(), TransformPluginType.class, Transform.class );

    parent = new LoggingObject( "Presentation unit test" );

    // Create SVG output folder if it doesn't exist
    //
    folderName = System.getProperty( "java.io.tmpdir" ) + "/Lean/";
    File folder = new File( folderName );
    if ( !folder.exists() ) {
      folder.mkdirs();
    }
  }

  @After
  public void tearDown() throws Exception {
  }

  @Ignore
  protected void testRendering( LeanPresentation presentation, String filename ) throws Exception {
    IRenderContext renderContext = new SimpleRenderContext( 500, 500, presentation.getThemes() );
    IDataContext dataContext = new PresentationDataContext( presentation, metadataProvider );

    LeanLayoutResults results = presentation.doLayout( parent, renderContext, metadataProvider );
    presentation.render( results, metadataProvider );

    results.saveSvgPages( folderName, filename, true, true, true );

    LeanRenderPage leanRenderPage = results.getRenderPages().get( 0 );
    String xml = leanRenderPage.getSvgXml();
    assertNotNull( xml );
  }
}