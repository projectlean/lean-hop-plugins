package org.lean.render.svg;

import org.lean.presentation.LeanPresentation;
import org.lean.presentation.util.TableFromHopPresentationUtil;
import org.junit.Test;

public class LeanPresentationTableFromHopTest extends LeanPresentationTestBase {
  
  @Test
  public void testTableRender() throws Exception {

    LeanPresentation presentation = TableFromHopPresentationUtil.createTableFromHopPresentation( 2900 );
    testRendering( presentation, "table_from_kettle");
  }

}