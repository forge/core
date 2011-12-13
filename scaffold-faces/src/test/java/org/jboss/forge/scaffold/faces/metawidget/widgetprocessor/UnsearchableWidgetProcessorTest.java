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
package org.jboss.forge.scaffold.faces.metawidget.widgetprocessor;

import static org.metawidget.inspector.InspectionResultConstants.*;
import static org.metawidget.inspector.faces.StaticFacesInspectionResultConstants.*;

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
   }
}
