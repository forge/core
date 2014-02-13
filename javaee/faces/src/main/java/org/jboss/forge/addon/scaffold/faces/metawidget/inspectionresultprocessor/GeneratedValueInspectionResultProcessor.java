/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.faces.metawidget.inspectionresultprocessor;

import static org.jboss.forge.addon.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.GENERATED_VALUE;
import static org.jboss.forge.addon.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.PRIMARY_KEY;
import static org.metawidget.inspector.InspectionResultConstants.FALSE;
import static org.metawidget.inspector.InspectionResultConstants.HIDDEN;
import static org.metawidget.inspector.InspectionResultConstants.TRUE;

import java.util.Map;

import org.metawidget.inspectionresultprocessor.impl.BaseInspectionResultProcessor;

/**
 * Processes the inspection result to treat @GeneratedValue fields as hidden, otherwise as not hidden. See FORGE-870.
 *
 * @author Vineet Reynolds
 */

public class GeneratedValueInspectionResultProcessor<M> extends BaseInspectionResultProcessor<M>
{
   //
   // Protected methods
   //

   @Override
   protected void processAttributes(Map<String, String> attributes, M metawidget)
   {
      boolean isPrimaryKey = attributes.get(PRIMARY_KEY) == null ? false : attributes.get(PRIMARY_KEY).equals(TRUE);
      boolean isGeneratedValue = attributes.get(GENERATED_VALUE) == null ? false: attributes.get(GENERATED_VALUE).equals(TRUE);
      if (isPrimaryKey)
      {
         if (isGeneratedValue)
         {
            attributes.put(HIDDEN, TRUE);
         }
         else
         {
            attributes.put(HIDDEN, FALSE);
         }
      }
   }
}
