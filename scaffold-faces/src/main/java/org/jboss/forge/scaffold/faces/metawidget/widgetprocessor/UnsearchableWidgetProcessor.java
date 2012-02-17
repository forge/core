// Metawidget
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

package org.jboss.forge.scaffold.faces.metawidget.widgetprocessor;

import static org.metawidget.inspector.faces.StaticFacesInspectionResultConstants.*;

import java.util.Map;

import org.metawidget.statically.StaticMetawidget;
import org.metawidget.statically.StaticWidget;
import org.metawidget.statically.StaticXmlStub;
import org.metawidget.statically.javacode.StaticJavaStub;
import org.metawidget.util.ClassUtils;
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
<<<<<<< HEAD:scaffold-faces/src/main/java/org/jboss/forge/scaffold/faces/metawidget/widgetprocessor/UnsearchableWidgetProcessor.java
      Integer widgetsProcessed = metawidget.getClientProperty(UnsearchableWidgetProcessor.class);
=======
      int widgetsProcessed = metawidget.getClientProperty(UnsearchableWidgetProcessor.class);
>>>>>>> a59830d... UI code cleanup:scaffold-faces/src/main/java/org/jboss/forge/scaffold/faces/metawidget/widgetprocessor/UnsearchableWidgetProcessor.java

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
         String type = WidgetBuilderUtils.getActualClassOrType(attributes);

         if (type != null)
         {
            Class<?> clazz = ClassUtils.niceForName(type);

            if (String.class.equals(clazz) || int.class.equals(clazz))
            {
               return true;
            }
         }

         if (attributes.containsKey(FACES_LOOKUP))
         {
            return true;
         }
      }

      return false;
   }
}
