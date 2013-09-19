/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.faces.metawidget.inspectionresultprocessor;

import static org.metawidget.inspector.InspectionResultConstants.REQUIRED;

import java.util.Map;

import org.metawidget.inspectionresultprocessor.impl.BaseInspectionResultProcessor;
import org.metawidget.statically.StaticXmlWidget;

/**
 * Stops fields from being marked 'required' fields. See FORGE-468.
 *
 * @author Richard Kennard
 */

public class NotRequiredInspectionResultProcessor
         extends BaseInspectionResultProcessor<StaticXmlWidget>
{
   //
   // Protected methods
   //

   @Override
   protected void processAttributes(Map<String, String> attributes, StaticXmlWidget metawidget)
   {
      attributes.put(REQUIRED, null);
   }
}
