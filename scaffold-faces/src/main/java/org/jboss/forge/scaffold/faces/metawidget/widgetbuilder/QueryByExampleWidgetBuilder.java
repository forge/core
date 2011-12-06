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
import org.metawidget.statically.StaticStub;
import org.metawidget.statically.StaticWidget;
import org.metawidget.statically.javacode.JavaStatement;
import org.metawidget.statically.javacode.StaticJavaMetawidget;
import org.metawidget.util.ClassUtils;
import org.metawidget.util.WidgetBuilderUtils;
import org.metawidget.util.simple.StringUtils;
import org.metawidget.widgetbuilder.iface.WidgetBuilder;

@Veto
public class QueryByExampleWidgetBuilder
         implements WidgetBuilder<StaticWidget, StaticJavaMetawidget>
{
   //
   // Public methods
   //

   @Override
   public StaticWidget buildWidget(String elementName, Map<String, String> attributes, StaticJavaMetawidget metawidget)
   {
      // Hidden

      if (TRUE.equals(attributes.get(HIDDEN)))
      {
         return new StaticStub();
      }

      String type = WidgetBuilderUtils.getActualClassOrType(attributes);

      // If no type, fail gracefully

      if (type == null)
      {
         return new StaticStub();
      }

      // Lookup the Class

      Class<?> clazz = ClassUtils.niceForName(type);

      // String

      if (String.class.equals(clazz))
      {
         String name = attributes.get(NAME);

         StaticWidget toReturn = new StaticStub();
         toReturn.getChildren().add(
                  new JavaStatement("String " + name + " = this.search.get" + StringUtils.capitalize(name) + "()"));
         JavaStatement ifNotEmpty = new JavaStatement("if (" + name + " != null && !\"\".equals(" + name + "))");
         ifNotEmpty.getChildren().add(
                  new JavaStatement("predicatesList.add(builder.like(root.<String>get(\"" + name + "\"), '%' + " + name + " + '%'))"));
         toReturn.getChildren().add(ifNotEmpty);
         return toReturn;
      }

      // int

      if (int.class.equals(clazz))
      {
         String name = attributes.get(NAME);

         StaticWidget toReturn = new StaticStub();
         toReturn.getChildren().add(
                  new JavaStatement("int " + name + " = this.search.get" + StringUtils.capitalize(name) + "()"));
         JavaStatement ifNotEmpty = new JavaStatement("if (" + name + " != 0)");
         ifNotEmpty.getChildren().add(
                  new JavaStatement("predicatesList.add(builder.equal(root.get(\"" + name + "\")," + name + "))"));
         toReturn.getChildren().add(ifNotEmpty);
         return toReturn;
      }

      // Lookup

      if (attributes.containsKey(FACES_LOOKUP))
      {
         String name = attributes.get(NAME);

         StaticWidget toReturn = new StaticStub();
         // TODO: just use simpleName, and add to imports
         toReturn.getChildren().add(
                  new JavaStatement(type + " " + name + " = this.search.get" + StringUtils.capitalize(name) + "()"));
         JavaStatement ifNotEmpty = new JavaStatement("if (" + name + " != null && " + name + ".getId() != null)");
         ifNotEmpty.getChildren().add(
                  new JavaStatement("predicatesList.add(builder.equal(root.get(\"" + name + "\")," + name + "))"));
         toReturn.getChildren().add(ifNotEmpty);
         return toReturn;
      }

      // Do not recurse sub-entities for now

      if (!ENTITY.equals(elementName))
      {
         return new StaticStub();
      }

      return null;
   }
}
