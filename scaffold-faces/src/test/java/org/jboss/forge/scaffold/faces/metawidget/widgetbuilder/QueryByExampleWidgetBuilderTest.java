/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.scaffold.faces.metawidget.widgetbuilder;

import static org.metawidget.inspector.InspectionResultConstants.*;
import static org.metawidget.inspector.faces.StaticFacesInspectionResultConstants.*;

import java.util.Map;

import junit.framework.TestCase;

import org.metawidget.statically.StaticWidget;
import org.metawidget.statically.javacode.StaticJavaMetawidget;
import org.metawidget.util.CollectionUtils;

public class QueryByExampleWidgetBuilderTest
         extends TestCase
{
   //
   // Public methods
   //

   public void testSuppress()
   {
      QueryByExampleWidgetBuilder widgetBuilder = new QueryByExampleWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "abc");
      attributes.put(TYPE, String.class.getName());
      attributes.put(HIDDEN, TRUE);
      assertEquals("", widgetBuilder.buildWidget(PROPERTY, attributes, new StaticJavaMetawidget()).toString());
   }

   public void testString()
   {
      QueryByExampleWidgetBuilder widgetBuilder = new QueryByExampleWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "abc");
      attributes.put(TYPE, String.class.getName());
      StaticWidget widget = widgetBuilder.buildWidget(PROPERTY, attributes, new StaticJavaMetawidget());

      assertEquals(
               "String abc = this.search.getAbc();if (abc != null && !\"\".equals(abc)) { predicatesList.add(builder.like(root.<String>get(\"abc\"), '%' + abc + '%')); }",
               widget.toString());
   }

   public void testInt()
   {
      QueryByExampleWidgetBuilder widgetBuilder = new QueryByExampleWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "abc");
      attributes.put(TYPE, int.class.getName());
      StaticWidget widget = widgetBuilder.buildWidget(PROPERTY, attributes, new StaticJavaMetawidget());

      assertEquals(
               "int abc = this.search.getAbc();if (abc != 0) { predicatesList.add(builder.equal(root.get(\"abc\"), abc)); }",
               widget.toString());
   }

   public void testLookup()
   {
      QueryByExampleWidgetBuilder widgetBuilder = new QueryByExampleWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "abc");
      attributes.put(TYPE, "com.test.domain.Foo");
      attributes.put(LOOKUP, "ONE,TWO,THREE");
      StaticWidget widget = widgetBuilder.buildWidget(PROPERTY, attributes, new StaticJavaMetawidget());

      assertEquals(
               "Foo abc = this.search.getAbc();if (abc != null) { predicatesList.add(builder.equal(root.get(\"abc\"), abc)); }",
               widget.toString());
   }

   public void testFacesLookup()
   {
      QueryByExampleWidgetBuilder widgetBuilder = new QueryByExampleWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "abc");
      attributes.put(TYPE, "com.test.domain.Foo");
      attributes.put(FACES_LOOKUP, "#{foo.bar}");
      StaticWidget widget = widgetBuilder.buildWidget(PROPERTY, attributes, new StaticJavaMetawidget());

      assertEquals(
               "Foo abc = this.search.getAbc();if (abc != null) { predicatesList.add(builder.equal(root.get(\"abc\"), abc)); }",
               widget.toString());
   }
}
