/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.scaffold.faces.metawidget.widgetprocessor;

import static org.metawidget.inspector.InspectionResultConstants.PROPERTY;
import static org.metawidget.inspector.InspectionResultConstants.TYPE;
import static org.metawidget.inspector.faces.StaticFacesInspectionResultConstants.FACES_LOOKUP;

import java.util.Map;

import junit.framework.TestCase;

import org.metawidget.statically.StaticMetawidget;
import org.metawidget.statically.StaticXmlMetawidget;
import org.metawidget.statically.StaticXmlStub;
import org.metawidget.statically.faces.component.html.StaticHtmlMetawidget;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlInputText;
import org.metawidget.statically.javacode.JavaStatement;
import org.metawidget.statically.javacode.StaticJavaMetawidget;
import org.metawidget.statically.javacode.StaticJavaStub;
import org.metawidget.util.CollectionUtils;

public class UnsearchableWidgetProcessorTest
         extends TestCase
{
   //
   // Public methods
   //

   public void testPassthrough()
   {
      StaticXmlMetawidget metawidget = new StaticHtmlMetawidget();
      UnsearchableWidgetProcessor widgetProcessor = new UnsearchableWidgetProcessor();
      widgetProcessor.onStartBuild(metawidget);

      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(TYPE, String.class.getName());
      assertTrue(widgetProcessor.processWidget(new HtmlInputText(), PROPERTY, attributes, metawidget) != null);

      attributes.put(TYPE, int.class.getName());
      assertTrue(widgetProcessor.processWidget(new HtmlInputText(), PROPERTY, attributes, metawidget) != null);

      attributes.put(TYPE, "com.test.domain.Foo");
      attributes.put(FACES_LOOKUP, String.class.getName());
      assertTrue(widgetProcessor.processWidget(new HtmlInputText(), PROPERTY, attributes, metawidget) != null);
   }

   public void testSuppressWrongType()
   {
      StaticXmlMetawidget metawidget = new StaticHtmlMetawidget();
      UnsearchableWidgetProcessor widgetProcessor = new UnsearchableWidgetProcessor();
      widgetProcessor.onStartBuild(metawidget);

      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(TYPE, "com.test.domain.Foo");
      assertTrue(widgetProcessor.processWidget(new HtmlInputText(), PROPERTY, attributes, metawidget) == null);
   }

   public void testSuppressTooMany()
   {
      // XML

      StaticMetawidget metawidget = new StaticHtmlMetawidget();
      UnsearchableWidgetProcessor widgetProcessor = new UnsearchableWidgetProcessor();
      widgetProcessor.onStartBuild(metawidget);

      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(TYPE, String.class.getName());
      assertTrue(widgetProcessor.processWidget(new HtmlInputText(), PROPERTY, attributes, metawidget) != null);
      assertTrue(widgetProcessor.processWidget(new HtmlInputText(), PROPERTY, attributes, metawidget) != null);
      assertTrue(widgetProcessor.processWidget(new HtmlInputText(), PROPERTY, attributes, metawidget) != null);
      assertTrue(widgetProcessor.processWidget(new HtmlInputText(), PROPERTY, attributes, metawidget) != null);
      assertTrue(widgetProcessor.processWidget(new StaticXmlStub(), PROPERTY, attributes, metawidget) != null);
      assertTrue(widgetProcessor.processWidget(new HtmlInputText(), PROPERTY, attributes, metawidget) != null);
      assertTrue(widgetProcessor.processWidget(new HtmlInputText(), PROPERTY, attributes, metawidget) == null);

      // Java

      metawidget = new StaticJavaMetawidget();
      widgetProcessor.onStartBuild(metawidget);

      assertTrue(widgetProcessor.processWidget(new JavaStatement("foo"), PROPERTY, attributes, metawidget) != null);
      assertTrue(widgetProcessor.processWidget(new JavaStatement("foo"), PROPERTY, attributes, metawidget) != null);
      assertTrue(widgetProcessor.processWidget(new JavaStatement("foo"), PROPERTY, attributes, metawidget) != null);
      assertTrue(widgetProcessor.processWidget(new JavaStatement("foo"), PROPERTY, attributes, metawidget) != null);
      assertTrue(widgetProcessor.processWidget(new StaticJavaStub(), PROPERTY, attributes, metawidget) != null);
      assertTrue(widgetProcessor.processWidget(new JavaStatement("foo"), PROPERTY, attributes, metawidget) != null);
      assertTrue(widgetProcessor.processWidget(new JavaStatement("foo"), PROPERTY, attributes, metawidget) == null);

      metawidget = new StaticJavaMetawidget();
      widgetProcessor.onStartBuild(metawidget);

      StaticJavaStub nonEmptyStub = new StaticJavaStub();
      nonEmptyStub.getChildren().add(new JavaStatement("foo"));
      assertTrue(widgetProcessor.processWidget(nonEmptyStub, PROPERTY, attributes, metawidget) != null);
      assertTrue(widgetProcessor.processWidget(nonEmptyStub, PROPERTY, attributes, metawidget) != null);
      assertTrue(widgetProcessor.processWidget(nonEmptyStub, PROPERTY, attributes, metawidget) != null);
      assertTrue(widgetProcessor.processWidget(nonEmptyStub, PROPERTY, attributes, metawidget) != null);
      assertTrue(widgetProcessor.processWidget(new StaticJavaStub(), PROPERTY, attributes, metawidget) != null);
      assertTrue(widgetProcessor.processWidget(nonEmptyStub, PROPERTY, attributes, metawidget) != null);
      assertTrue(widgetProcessor.processWidget(nonEmptyStub, PROPERTY, attributes, metawidget) == null);
   }
}
