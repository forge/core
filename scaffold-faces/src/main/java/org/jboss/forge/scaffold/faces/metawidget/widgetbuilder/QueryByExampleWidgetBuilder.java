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
import org.metawidget.statically.javacode.JavaStatement;
import org.metawidget.statically.javacode.StaticJavaMetawidget;
import org.metawidget.statically.javacode.StaticJavaStub;
import org.metawidget.statically.javacode.StaticJavaWidget;
import org.metawidget.util.ClassUtils;
import org.metawidget.util.WidgetBuilderUtils;
import org.metawidget.util.simple.StringUtils;
import org.metawidget.widgetbuilder.iface.WidgetBuilder;

@Veto
public class QueryByExampleWidgetBuilder
         implements WidgetBuilder<StaticJavaWidget, StaticJavaMetawidget>
{
   //
   // Public methods
   //

   // TODO: limit to 5 fields
   // TODO: do n_to_many too

   @Override
   public StaticJavaWidget buildWidget(String elementName, Map<String, String> attributes, StaticJavaMetawidget metawidget)
   {
      // Hidden

      if (TRUE.equals(attributes.get(HIDDEN)))
      {
         return new StaticJavaStub();
      }

      String type = WidgetBuilderUtils.getActualClassOrType(attributes);

      // If no type, fail gracefully

      if (type == null)
      {
         return new StaticJavaStub();
      }

      // Lookup the Class

      Class<?> clazz = ClassUtils.niceForName(type);
      String name = attributes.get(NAME);

      // String

      if (String.class.equals(clazz))
      {
         StaticJavaStub toReturn = new StaticJavaStub();
         toReturn.getChildren().add(
                  new JavaStatement("String " + name + " = this.search.get" + StringUtils.capitalize(name) + "()"));
         JavaStatement ifNotEmpty = new JavaStatement("if (" + name + " != null && !\"\".equals(" + name + "))");
         ifNotEmpty.getChildren().add(
                  new JavaStatement("predicatesList.add(builder.like(root.<String>get(\"" + name + "\"), '%' + " + name
                           + " + '%'))"));
         toReturn.getChildren().add(ifNotEmpty);
         return toReturn;
      }

      // int

      if (int.class.equals(clazz))
      {
         StaticJavaStub toReturn = new StaticJavaStub();
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
         StaticJavaStub toReturn = new StaticJavaStub();
         JavaStatement getValue = new JavaStatement(ClassUtils.getSimpleName(type) + " " + name + " = this.search.get"
                  + StringUtils.capitalize(name) + "()");
         getValue.putImport(type);
         toReturn.getChildren().add(getValue);
         // Need to use .getId() != null until https://issues.jboss.org/browse/FORGE-401
         JavaStatement ifNotEmpty = new JavaStatement("if (" + name + " != null && " + name + ".getId() != null)");
         ifNotEmpty.getChildren().add(
                  new JavaStatement("predicatesList.add(builder.equal(root.get(\"" + name + "\")," + name + "))"));
         toReturn.getChildren().add(ifNotEmpty);
         return toReturn;
      }

      // Do not recurse sub-entities for now

      if (!ENTITY.equals(elementName))
      {
         return new StaticJavaStub();
      }

      return null;
   }
}
