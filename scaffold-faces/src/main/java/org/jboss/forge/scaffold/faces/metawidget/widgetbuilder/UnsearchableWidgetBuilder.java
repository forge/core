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

package org.jboss.forge.scaffold.faces.metawidget.widgetbuilder;

import static org.metawidget.inspector.InspectionResultConstants.*;
import static org.metawidget.inspector.faces.StaticFacesInspectionResultConstants.*;

import java.util.Map;

import org.jboss.solder.core.Veto;
import org.metawidget.statically.StaticMetawidget;
import org.metawidget.statically.StaticWidget;
import org.metawidget.statically.StaticXmlStub;
import org.metawidget.statically.javacode.StaticJavaStub;
import org.metawidget.statically.javacode.StaticJavaWidget;
import org.metawidget.util.ClassUtils;
import org.metawidget.util.WidgetBuilderUtils;
import org.metawidget.widgetbuilder.iface.WidgetBuilder;

/**
 * WidgetBuilder to suppress fields that are not suitable as search fields.
 *
 * @author Richard Kennard
 */

@Veto
public class UnsearchableWidgetBuilder
         implements WidgetBuilder<StaticWidget, StaticMetawidget>
{
   //
   // Private statics
   //

   private final static int MAXIMUM_SEARCH_FIELDS = 5;

   //
   // Public methods
   //

   @Override
   public StaticWidget buildWidget(String elementName, Map<String, String> attributes, StaticMetawidget metawidget)
   {
      // Drill down

      if (ENTITY.equals(elementName))
      {
         return null;
      }

      // Suppress

      if (TRUE.equals(attributes.get(HIDDEN)))
      {
         return newStaticStub(metawidget);
      }

      if (metawidget.getChildren().size() >= MAXIMUM_SEARCH_FIELDS)
      {
         return newStaticStub(metawidget);
      }

      // Pass through

      if (!WidgetBuilderUtils.isReadOnly(attributes))
      {
         String type = WidgetBuilderUtils.getActualClassOrType(attributes);

         if (type != null)
         {
            Class<?> clazz = ClassUtils.niceForName(type);

            if (String.class.equals(clazz) || int.class.equals(clazz))
            {
               return null;
            }
         }

         if (attributes.containsKey(FACES_LOOKUP))
         {
            return null;
         }
      }

      // Suppress anything else

      return newStaticStub(metawidget);
   }

   //
   // Private methods
   //

   /**
    * @return the correct Stub type for the given Meawidget
    */

   private StaticWidget newStaticStub(StaticMetawidget metawidget)
   {
      if (metawidget instanceof StaticJavaWidget)
      {
         return new StaticJavaStub();
      }

      return new StaticXmlStub();
   }
}
