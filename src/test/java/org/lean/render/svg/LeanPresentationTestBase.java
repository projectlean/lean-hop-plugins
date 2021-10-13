package org.lean.render.svg;

import org.apache.hop.core.HopEnvironment;
import org.apache.hop.core.annotations.Transform;
import org.apache.hop.core.logging.ILoggingObject;
import org.apache.hop.core.logging.LoggingObject;
import org.apache.hop.core.plugins.PluginRegistry;
import org.apache.hop.core.plugins.TransformPluginType;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.metadata.serializer.memory.MemoryMetadataProvider;
import org.apache.hop.pipeline.transforms.csvinput.CsvInputMeta;
import org.apache.hop.pipeline.transforms.getfilenames.GetFileNamesMeta;
import org.apache.hop.pipeline.transforms.randomvalue.RandomValueMeta;
import org.apache.hop.pipeline.transforms.replacestring.ReplaceStringMeta;
import org.apache.hop.pipeline.transforms.rowgenerator.RowGeneratorMeta;
import org.apache.hop.pipeline.transforms.selectvalues.SelectValuesMeta;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.lean.core.LeanEnvironment;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.datacontext.IDataContext;
import org.lean.presentation.datacontext.PresentationDataContext;
import org.lean.presentation.layout.LeanLayoutResults;
import org.lean.presentation.layout.LeanRenderPage;
import org.lean.presentation.variable.LeanParameter;
import org.lean.render.IRenderContext;
import org.lean.render.context.SimpleRenderContext;

import java.io.File;
import java.util.ArrayList;

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
    HopEnvironment.init();

    PluginRegistry registry = PluginRegistry.getInstance();

    registry.registerPluginClass( CsvInputMeta.class.getName(), TransformPluginType.class, Transform.class );
    registry.registerPluginClass( SelectValuesMeta.class.getName(), TransformPluginType.class, Transform.class );
    registry.registerPluginClass( RandomValueMeta.class.getName(), TransformPluginType.class, Transform.class );
    registry.registerPluginClass( ReplaceStringMeta.class.getName(), TransformPluginType.class, Transform.class );
    registry.registerPluginClass( GetFileNamesMeta.class.getName(), TransformPluginType.class, Transform.class );
    registry.registerPluginClass( RowGeneratorMeta.class.getName(), TransformPluginType.class, Transform.class );

    assertNotNull( "CSVInput transform not in registry", registry.findPluginWithId( TransformPluginType.class, "CSVInput" )  );
    assertNotNull( "Dummy transform not in registry", registry.findPluginWithId( TransformPluginType.class, "Dummy" )  );
    assertNotNull( "Select Values transform not in registry", registry.findPluginWithId( TransformPluginType.class, "SelectValues" )  );
    assertNotNull( "Random Value transform not in registry", registry.findPluginWithId( TransformPluginType.class, "RandomValue" )  );
    assertNotNull( "Replace in String transform not in registry", registry.findPluginWithId( TransformPluginType.class, "ReplaceString" )  );
    assertNotNull( "Get File Names transform not in registry", registry.findPluginWithId( TransformPluginType.class, "GetFileNames" )  );
    assertNotNull( "Generate Rows transform not in registry", registry.findPluginWithId( TransformPluginType.class, "RowGenerator" )  );

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

    LeanLayoutResults results = presentation.doLayout( parent, renderContext, metadataProvider, new ArrayList<>() );
    presentation.render( results, metadataProvider );

    results.saveSvgPages( folderName, filename, true, true, true );

    if (results.getRenderPages().isEmpty()) {
      throw new Exception("We didn't get render pages for presentation: "+presentation.getName()+" and filename: "+filename);
    }
    LeanRenderPage leanRenderPage = results.getRenderPages().get( 0 );
    String xml = leanRenderPage.getSvgXml();
    assertNotNull( xml );
  }
}