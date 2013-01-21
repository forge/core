/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.scaffold.faces.metawidget.widgetbuilder;

import static org.metawidget.inspector.InspectionResultConstants.HIDDEN;
import static org.metawidget.inspector.InspectionResultConstants.LOOKUP;
import static org.metawidget.inspector.InspectionResultConstants.NAME;
import static org.metawidget.inspector.InspectionResultConstants.PROPERTY;
import static org.metawidget.inspector.InspectionResultConstants.TRUE;
import static org.metawidget.inspector.InspectionResultConstants.TYPE;
import static org.metawidget.inspector.faces.StaticFacesInspectionResultConstants.FACES_LOOKUP;

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
               "String abc = this.example.getAbc();if (abc != null && !\"\".equals(abc)) { predicatesList.add(builder.like(root.<String>get(\"abc\"), '%' + abc + '%')); }",
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
               "int abc = this.example.getAbc();if (abc != 0) { predicatesList.add(builder.equal(root.get(\"abc\"), abc)); }",
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
               "Foo abc = this.example.getAbc();if (abc != null) { predicatesList.add(builder.equal(root.get(\"abc\"), abc)); }",
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
               "Foo abc = this.example.getAbc();if (abc != null) { predicatesList.add(builder.equal(root.get(\"abc\"), abc)); }",
               widget.toString());
   }
}
