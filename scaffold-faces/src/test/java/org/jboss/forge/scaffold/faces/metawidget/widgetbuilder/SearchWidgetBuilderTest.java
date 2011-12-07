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

import org.metawidget.statically.StaticXmlMetawidget;
import org.metawidget.statically.StaticXmlStub;
import org.metawidget.statically.faces.component.html.StaticHtmlMetawidget;
import org.metawidget.util.CollectionUtils;

public class SearchWidgetBuilderTest
         extends TestCase
{
   //
   // Public methods
   //

   public void testPassthrough()
   {
      SearchWidgetBuilder widgetBuilder = new SearchWidgetBuilder();
      assertEquals(null, widgetBuilder.buildWidget(ENTITY, null, null));

      StaticXmlMetawidget metawidget = new StaticHtmlMetawidget();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(TYPE, String.class.getName());
      assertEquals(null, widgetBuilder.buildWidget(PROPERTY, attributes, metawidget));

      attributes.put(TYPE, int.class.getName());
      assertEquals(null, widgetBuilder.buildWidget(PROPERTY, attributes, metawidget));

      attributes.put(TYPE, "com.test.domain.Foo");
      attributes.put(FACES_LOOKUP, String.class.getName());
      assertEquals(null, widgetBuilder.buildWidget(PROPERTY, attributes, metawidget));
   }

   public void testSuppress()
   {
      SearchWidgetBuilder widgetBuilder = new SearchWidgetBuilder();
      StaticXmlMetawidget metawidget = new StaticHtmlMetawidget();
      Map<String, String> attributes = CollectionUtils.newHashMap();

      attributes.put(TYPE, "com.test.domain.Foo");
      assertTrue(widgetBuilder.buildWidget(PROPERTY, attributes, metawidget) instanceof StaticXmlStub);
   }
}
