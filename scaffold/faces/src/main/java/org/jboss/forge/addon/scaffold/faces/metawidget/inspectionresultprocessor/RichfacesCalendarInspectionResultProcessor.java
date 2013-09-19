/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.faces.metawidget.inspectionresultprocessor;

import static org.metawidget.inspector.InspectionResultConstants.DATETIME_PATTERN;
import static org.metawidget.inspector.InspectionResultConstants.DATETIME_TYPE;
import java.util.Map;

import org.metawidget.inspectionresultprocessor.impl.BaseInspectionResultProcessor;

/**
 * Processes the inspection result to add additional semantic information for the MetaWidget Richfaces widgetbuilder.
 * See FORGE-913.
 * 
 * @author Vineet Reynolds
 */

public class RichfacesCalendarInspectionResultProcessor<M> extends BaseInspectionResultProcessor<M>
{
   //
   // Protected methods
   //

   @Override
   protected void processAttributes(Map<String, String> attributes, M metawidget)
   {
      String dateTimeType = attributes.get(DATETIME_TYPE);
      if(dateTimeType != null && dateTimeType.equals("both"))
      {
         attributes.put(DATETIME_PATTERN, "MMM d, yyyy hh:mm:ss a");
      }
   }
}
