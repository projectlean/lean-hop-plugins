package org.lean.presentation.util;

import org.lean.core.LeanAttachment;
import org.lean.core.LeanColorRGB;
import org.lean.core.LeanColumn;
import org.lean.core.LeanFont;
import org.lean.core.LeanHorizontalAlignment;
import org.lean.core.LeanVerticalAlignment;
import org.lean.presentation.LeanPresentation;
import org.lean.presentation.component.LeanComponent;
import org.lean.presentation.component.pipeline.LeanPipelineComponent;
import org.lean.presentation.component.types.table.LeanTableComponent;
import org.lean.presentation.component.workflow.pipeline.LeanWorkflowComponent;
import org.lean.presentation.connector.LeanConnector;
import org.lean.presentation.connector.hop.LeanPipelineConnector;
import org.lean.presentation.layout.LeanLayout;
import org.lean.presentation.page.LeanPage;

import java.util.Arrays;
import java.util.List;

public class TableFromHopPresentationUtil extends BasePresentationUtil {

  private static final String COMPONENT_NAME_TABLE = "Table1";
  private static final String CONNECTOR_NAME_HOP = "Hop";

  private static final String COMPONENT_NAME_PIPELINE = "Pipeline1";
  private static final String COMPONENT_NAME_WORKFLOW = "Workflow";

  public static LeanPresentation createTableFromHopPresentation( int nr ) throws Exception {

    LeanPresentation presentation = createBasePresentation(
      "Table (" + nr + ")",
      "Table " + nr + " description",
      100,
      "A table with a label right below that",
      true
    );

    LeanPage pageOne = presentation.getPages().get( 0 );

    // Add a Lean Kettle Connector
    //
    String transformationFilename = "src/test/resources/pipelines/read-customer-csv-data.hpl";
    String outputStepname = "OUTPUT";
    LeanPipelineConnector leanPipelineConnector = new LeanPipelineConnector( transformationFilename, outputStepname );
    LeanConnector kettleConnector = new LeanConnector( CONNECTOR_NAME_HOP, leanPipelineConnector );
    presentation.getConnectors().add(kettleConnector);

    // Add the table
    List<LeanColumn> columnSelection = Arrays.asList(
      new LeanColumn( "id", "ID", LeanHorizontalAlignment.RIGHT, LeanVerticalAlignment.TOP ),
      new LeanColumn( "name", "Last name", LeanHorizontalAlignment.LEFT, LeanVerticalAlignment.TOP ),
      new LeanColumn( "firstname", "First name", LeanHorizontalAlignment.LEFT, LeanVerticalAlignment.TOP ),
      new LeanColumn( "birthdate", "Birth date", LeanHorizontalAlignment.CENTER, LeanVerticalAlignment.TOP )
    );

    columnSelection.get( 3 ).setFormatMask( "yyyy/MM/dd" );


    LeanTableComponent table = new LeanTableComponent( CONNECTOR_NAME_HOP, columnSelection );
    table.setBorder( false );
    table.setHorizontalMargin( 4 );
    table.setVerticalMargin( 2 );
    table.setDefaultColor( new LeanColorRGB( 80, 80, 80 ) );
    table.setBorderColor( new LeanColorRGB( 120, 120, 120 ) );
    table.setBackground( false );
    table.setBackGroundColor( new LeanColorRGB( 220, 220, 220 ) );
    table.setGridColor( new LeanColorRGB( 180, 180, 180 ) );
    table.setDefaultFont( new LeanFont( "TimesRoman", "16", false, false ) );
    table.setHeaderFont( new LeanFont( "Arial", "16", true, false ) );
    table.setEvenHeights( true );
    table.setHeader( true );
    table.setHeaderOnEveryPage( true );
    table.setGridLineWidth( "0.2" );

    LeanComponent table1 = new LeanComponent( COMPONENT_NAME_TABLE, table );
    LeanLayout tableLayout = new LeanLayout( 0, 0 );
    table1.setLayout( tableLayout );

    pageOne.getComponents().add( table1 );

    return presentation;
  }

  public static LeanPresentation createPipelinePresentation( int nr ) throws Exception {

    LeanPresentation presentation = createBasePresentation(
      "Pipeline (" + nr + ")",
      "Pipeline " + nr + " description",
      100,
      "Renders a Hop pipeline",
      true
    );

    LeanPage pageOne = presentation.getPages().get( 0 );

    String pipelineFilename = "src/test/resources/pipelines/read-customer-csv-data.hpl";

    LeanPipelineComponent lpc = new LeanPipelineComponent( pipelineFilename );
    lpc.setBorder( false );

    LeanComponent lc = new LeanComponent( COMPONENT_NAME_PIPELINE, lpc );
    LeanLayout pcLayout = new LeanLayout();
    pcLayout.setLeft( new LeanAttachment(0, 0) );
    pcLayout.setRight( new LeanAttachment(100, 0) );
    pcLayout.setTop( new LeanAttachment(0, 0) );
    pcLayout.setBottom( new LeanAttachment(100, 0) );
    lc.setLayout( pcLayout );

    pageOne.getComponents().add( lc );

    return presentation;
  }


  public static LeanPresentation createWorkflowPresentation( int nr ) throws Exception {

    LeanPresentation presentation = createBasePresentation(
      "Workflow (" + nr + ")",
      "Workflow " + nr + " description",
      100,
      "Renders a Hop Workflow",
      true
    );

    LeanPage pageOne = presentation.getPages().get( 0 );

    String filename = "src/test/resources/workflows/execute-pipelines.hwf";

    LeanWorkflowComponent lwc = new LeanWorkflowComponent( filename );
    lwc.setBorder( false );

    LeanComponent lc = new LeanComponent( COMPONENT_NAME_WORKFLOW, lwc );
    LeanLayout pcLayout = new LeanLayout();
    pcLayout.setLeft( new LeanAttachment(0, 0) );
    pcLayout.setRight( new LeanAttachment(100, 0) );
    pcLayout.setTop( new LeanAttachment(0, 0) );
    pcLayout.setBottom( new LeanAttachment(100, 0) );
    lc.setLayout( pcLayout );

    pageOne.getComponents().add( lc );

    return presentation;
  }

}
