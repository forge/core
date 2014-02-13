/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.faces.metawidget.widgetprocessor;

import static org.metawidget.inspector.InspectionResultConstants.LOOKUP;
import static org.metawidget.inspector.faces.StaticFacesInspectionResultConstants.FACES_LOOKUP;

import java.util.Map;

import org.metawidget.statically.StaticMetawidget;
import org.metawidget.statically.StaticWidget;
import org.metawidget.statically.StaticXmlStub;
import org.metawidget.statically.javacode.StaticJavaStub;
import org.metawidget.util.WidgetBuilderUtils;
import org.metawidget.widgetprocessor.iface.AdvancedWidgetProcessor;

/**
 * WidgetProcessor to suppress having too many search fields.
 *
 * @author Richard Kennard
 */

public class UnsearchableWidgetProcessor
         implements AdvancedWidgetProcessor<StaticWidget, StaticMetawidget>
{
   //
   // Private statics
   //

   private final static int MAXIMUM_SEARCH_FIELDS = 5;

   //
   // Public methods
   //

   @Override
   public void onStartBuild(StaticMetawidget metawidget)
   {
      metawidget.putClientProperty(UnsearchableWidgetProcessor.class, 0);
   }

   @Override
   public StaticWidget processWidget(StaticWidget widget, String elementName, Map<String, String> attributes,
            StaticMetawidget metawidget)
   {
      Integer widgetsProcessed = metawidget.getClientProperty(UnsearchableWidgetProcessor.class);

      // Ignore empty stubs

      if (widget instanceof StaticXmlStub || widget instanceof StaticJavaStub)
      {
         if (widget.getChildren().isEmpty())
         {
            return widget;
         }
      }

      // Too many?

      if (widgetsProcessed == MAXIMUM_SEARCH_FIELDS)
      {
         return null;
      }

      // Wrong type?

      if (!isSearchable(attributes))
      {
         return null;
      }

      // Pass through

      metawidget.putClientProperty(UnsearchableWidgetProcessor.class, widgetsProcessed + 1);
      return widget;
   }

   @Override
   public void onEndBuild(StaticMetawidget metawidget)
   {
      // Do nothing
   }

   //
   // Private methods
   //

   private boolean isSearchable(Map<String, String> attributes)
   {
      if (!WidgetBuilderUtils.isReadOnly(attributes))
      {
         Class<?> clazz = WidgetBuilderUtils.getActualClassOrType(attributes,null);

         if (String.class.equals(clazz) || int.class.equals(clazz) || short.class.equals(clazz)
                  || byte.class.equals(clazz) || long.class.equals(clazz) || Integer.class.equals(clazz)
                  || Short.class.equals(clazz) || Byte.class.equals(clazz) || Long.class.equals(clazz))
         {
            return true;
         }

         if (attributes.containsKey(LOOKUP) || attributes.containsKey(FACES_LOOKUP))
         {
            return true;
         }
      }

      return false;
   }
}
