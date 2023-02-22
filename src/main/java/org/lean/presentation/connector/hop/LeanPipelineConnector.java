package org.lean.presentation.connector.hop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.hop.core.HopEnvironment;
import org.apache.hop.core.exception.HopTransformException;
import org.apache.hop.core.logging.LogLevel;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.metadata.api.HopMetadataProperty;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.engine.IEngineComponent;
import org.apache.hop.pipeline.engine.IPipelineEngine;
import org.apache.hop.pipeline.engines.local.LocalPipelineEngine;
import org.apache.hop.pipeline.transform.RowAdapter;
import org.apache.hop.pipeline.transform.TransformMeta;
import org.lean.core.ILeanRowListener;
import org.lean.core.exception.LeanException;
import org.lean.presentation.connector.type.ILeanConnector;
import org.lean.presentation.connector.type.LeanBaseConnector;
import org.lean.presentation.connector.type.LeanConnectorPlugin;
import org.lean.presentation.datacontext.IDataContext;

import java.util.List;

@LeanConnectorPlugin( id = "LeanPipelineConnector", name = "Kettle", description = "Read from a Kettle transformation" )
public class LeanPipelineConnector extends LeanBaseConnector implements ILeanConnector {

  @HopMetadataProperty
  private String pipelineFilename;

  @HopMetadataProperty
  private String outputTransformName;

  public LeanPipelineConnector() {
    super( "LeanPipelineConnector" );
  }

  public LeanPipelineConnector( String pipelineFilename, String outputTransformName ) {
    this();
    this.pipelineFilename = pipelineFilename;
    this.outputTransformName = outputTransformName;
  }

  public LeanPipelineConnector( LeanPipelineConnector c ) {
    super( c );
    this.pipelineFilename = c.pipelineFilename;
    this.outputTransformName = c.outputTransformName;
  }

  @Override public LeanBaseConnector clone() {
    return new LeanPipelineConnector( this );
  }

  @JsonIgnore
  private transient IPipelineEngine<PipelineMeta> pipeline;

  @Override public void startStreaming( IDataContext dataContext ) throws LeanException {
    IVariables variables = dataContext.getVariables();
    final String realPipelineFilename = variables.resolve( pipelineFilename );
    final String realOutputTransformName = variables.resolve( outputTransformName );

    try {
      HopEnvironment.init();

      PipelineMeta pipelineMeta = loadPipelineMeta( dataContext );
      pipeline = new LocalPipelineEngine( pipelineMeta );
      pipelineMeta.setInternalHopVariables( pipeline );
      pipeline.setLogLevel( LogLevel.BASIC ); // TODO make configurable
      pipeline.prepareExecution();

      // Add a row listener to all transform copies
      //
      List<IEngineComponent> componentCopies = pipeline.getComponentCopies( realOutputTransformName );
      for ( IEngineComponent componentCopy : componentCopies ) {
        componentCopy.addRowListener( new RowAdapter() {
          @Override public void rowWrittenEvent( IRowMeta rowMeta, Object[] row ) throws HopTransformException {

            // Pass the row to all the listeners for this component...
            //
            for ( ILeanRowListener listener : LeanPipelineConnector.super.getRowListeners() ) {
              try {
                synchronized ( listener ) {
                  listener.rowReceived( rowMeta, row );
                }
              } catch ( LeanException e ) {
                throw new HopTransformException( "Error passing row from Hop to Lean in pipeline '" + realPipelineFilename, e );
              }
            }
          }
        } );
      }
      pipeline.startThreads();
      pipeline.waitUntilFinished();

      outputDone();
    } catch ( Exception e ) {
      if ( pipeline != null ) {
        pipeline.stopAll();
      }
      throw new LeanException( "Error running pipeline '" + realPipelineFilename + "'", e );
    }
  }

  @Override public void waitUntilFinished() throws LeanException {
    // Handled above
  }

  public IRowMeta describeOutput( IDataContext dataContext ) throws LeanException {

    IVariables variables = dataContext.getVariables();
    String realTransFilename = variables.resolve( pipelineFilename );
    String realOutputStepname = variables.resolve( outputTransformName );

    PipelineMeta pipelineMeta = loadPipelineMeta( dataContext );

    TransformMeta outputTransformMeta = pipelineMeta.findTransform( realOutputStepname );
    if ( outputTransformMeta == null ) {
      throw new LeanException( "There is no transform called '" + outputTransformName + "' in pipeline '" + realTransFilename + "'" );
    }

    try {
      return pipelineMeta.getTransformFields( variables, outputTransformMeta );
    } catch ( Exception e ) {
      throw new LeanException( "Error getting output fields for transform '" + realOutputStepname + "' in pipeline '" + realTransFilename, e );
    }
  }

  private PipelineMeta loadPipelineMeta( IDataContext dataContext ) throws LeanException {

    IVariables variables = dataContext.getVariables();
    String realPipelineFilename = variables.resolve( pipelineFilename );

    try {
      PipelineMeta pipelineMeta = new PipelineMeta( realPipelineFilename, dataContext.getMetadataProvider(), variables );
      return pipelineMeta;
    } catch ( Exception e ) {
      throw new LeanException( "Unable to load pipeline file '" + realPipelineFilename + "'", e );
    }
  }
}
