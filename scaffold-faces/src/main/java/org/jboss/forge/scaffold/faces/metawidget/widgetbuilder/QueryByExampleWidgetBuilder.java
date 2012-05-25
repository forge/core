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

import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.*;
import static org.metawidget.inspector.InspectionResultConstants.*;
import static org.metawidget.inspector.faces.StaticFacesInspectionResultConstants.*;

import java.util.Map;

import org.metawidget.statically.javacode.JavaStatement;
import org.metawidget.statically.javacode.StaticJavaMetawidget;
import org.metawidget.statically.javacode.StaticJavaStub;
import org.metawidget.statically.javacode.StaticJavaWidget;
import org.metawidget.util.ClassUtils;
import org.metawidget.util.WidgetBuilderUtils;
import org.metawidget.util.simple.StringUtils;
import org.metawidget.widgetbuilder.iface.WidgetBuilder;

public class QueryByExampleWidgetBuilder
         implements WidgetBuilder<StaticJavaWidget, StaticJavaMetawidget>
{
   //
   // Public methods
   //

   @Override
   public StaticJavaWidget buildWidget(String elementName, Map<String, String> attributes,
            StaticJavaMetawidget metawidget)
   {
      // Drill down

      if (ENTITY.equals(elementName))
      {
         return null;
      }

      // Suppress

      if (TRUE.equals(attributes.get(HIDDEN)) && !Boolean.valueOf(attributes.get(PRIMARY_KEY_NOT_GENERATED)))
      {
         return new StaticJavaStub();
      }

      Class<?> clazz = WidgetBuilderUtils.getActualClassOrType(attributes, null);
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

      // int or short

      if (int.class.equals(clazz) || short.class.equals(clazz) || byte.class.equals(clazz))
      {
         StaticJavaStub toReturn = new StaticJavaStub();
         toReturn.getChildren().add(
                  new JavaStatement(clazz.getSimpleName() + " " + name + " = this.search.get"
                           + StringUtils.capitalize(name) + "()"));
         JavaStatement ifNotEmpty = new JavaStatement("if (" + name + " != 0)");
         ifNotEmpty.getChildren().add(
                  new JavaStatement("predicatesList.add(builder.equal(root.get(\"" + name + "\"), " + name + "))"));
         toReturn.getChildren().add(ifNotEmpty);
         return toReturn;
      }

      // LOOKUP

      String type = attributes.get(TYPE);

      if (attributes.containsKey(LOOKUP))
      {
         StaticJavaStub toReturn = new StaticJavaStub();
         JavaStatement getValue = new JavaStatement(ClassUtils.getSimpleName(type) + " " + name + " = this.search.get"
                  + StringUtils.capitalize(name) + "()");
         getValue.putImport(type);
         toReturn.getChildren().add(getValue);
         JavaStatement ifNotEmpty = new JavaStatement("if (" + name + " != null)");
         ifNotEmpty.getChildren().add(
                  new JavaStatement("predicatesList.add(builder.equal(root.get(\"" + name + "\"), " + name + "))"));
         toReturn.getChildren().add(ifNotEmpty);
         return toReturn;
      }

      // FACES_LOOKUP

      if (attributes.containsKey(FACES_LOOKUP))
      {
         StaticJavaStub toReturn = new StaticJavaStub();
         JavaStatement getValue = new JavaStatement(ClassUtils.getSimpleName(type) + " " + name + " = this.search.get"
                  + StringUtils.capitalize(name) + "()");
         getValue.putImport(type);
         toReturn.getChildren().add(getValue);
         JavaStatement ifNotEmpty = new JavaStatement("if (" + name + " != null)");
         ifNotEmpty.getChildren().add(
                  new JavaStatement("predicatesList.add(builder.equal(root.get(\"" + name + "\"), " + name + "))"));
         toReturn.getChildren().add(ifNotEmpty);
         return toReturn;
      }

      // We tried searching against N_TO_MANY relationships, but had the following problems:
      //
      // 1. Difficult to make JPA Criteria Builder search for 'a Set having all of the following items'. For 'a Set
      // having the following item' can do: predicatesList.add(root.join("customers").in(this.search.getCustomer()));
      // 2. Cumbersome to have a new class for this.search that only has a single Customer, as opposed to a Set
      // 3. Difficult to make JSF's h:selectOne* bind to a Set
      // 4. Difficult to make JSF's h:selectMany* appear as a single item dropdown
      //
      // So we've left it out for now

      return new StaticJavaStub();
   }
}
