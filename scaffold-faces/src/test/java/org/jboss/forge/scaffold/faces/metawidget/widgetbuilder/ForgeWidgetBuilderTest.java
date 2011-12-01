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

import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.*;
import static org.metawidget.inspector.InspectionResultConstants.*;
import static org.metawidget.inspector.faces.StaticFacesInspectionResultConstants.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.metawidget.inspector.annotation.UiComesAfter;
import org.metawidget.statically.StaticWidget;
import org.metawidget.statically.faces.component.html.StaticHtmlMetawidget;
import org.metawidget.util.CollectionUtils;

public class ForgeWidgetBuilderTest
         extends TestCase
{
   //
   // Public methods
   //

   public void testManyToOne()
            throws Exception
   {
      StaticHtmlMetawidget metawidget = new StaticHtmlMetawidget();
      metawidget.setValue("#{foo}");
      ForgeWidgetBuilder widgetBuilder = new ForgeWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "bar");
      attributes.put(READ_ONLY, TRUE);
      attributes.put(TYPE, "com.test.domain.Bar");
      attributes.put(FACES_LOOKUP, "#{barBean.all}");
      StaticWidget widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);

      String result = "<h:link outcome=\"/scaffold/bar/view\" value=\"#{foo.bar}\">";
      result += "<f:param name=\"id\" value=\"#{foo.bar.id}\"/>";
      result += "</h:link>";

      assertEquals(result, widget.toString());
   }

   public void testTopLevelList()
            throws Exception
   {
      StaticHtmlMetawidget metawidget = new StaticHtmlMetawidget();
      metawidget.setValue("#{foo}");
      ForgeWidgetBuilder widgetBuilder = new ForgeWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "bar");
      attributes.put(TYPE, List.class.getName());
      attributes.put(PARAMETERIZED_TYPE, Bar.class.getName());
      StaticWidget widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);

      // (this looks a little weird because 'Bar' is an inner class)

      String result = "<h:dataTable id=\"fooBar\" value=\"#{foo.bar}\" var=\"_item\">";
      result += "<h:column><f:facet name=\"header\"><h:outputText value=\"Name\"/></f:facet>";
      result += "<h:link outcome=\"/scaffold/forgeWidgetBuilderTest$Bar/view\" value=\"#{_item.name}\"><f:param name=\"id\" value=\"#{_item.id}\"/></h:link>";
      result += "</h:column>";
      result += "<h:column><f:facet name=\"header\"><h:outputText value=\"Description\"/></f:facet>";
      result += "<h:link outcome=\"/scaffold/forgeWidgetBuilderTest$Bar/view\" value=\"#{_item.description}\"><f:param name=\"id\" value=\"#{_item.id}\"/></h:link>";
      result += "</h:column>";
      result += "</h:dataTable>";

      assertEquals(result, widget.toString());
   }

   public void testEmbeddedSet()
            throws Exception
   {
      StaticHtmlMetawidget metawidget = new StaticHtmlMetawidget();
      metawidget.setValue("#{foo}");
      ForgeWidgetBuilder widgetBuilder = new ForgeWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "bar");
      attributes.put(TYPE, Set.class.getName());
      attributes.put(PARAMETERIZED_TYPE, Bar.class.getName());
      attributes.put(ONE_TO_MANY, TRUE);
      StaticWidget widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);

      // (this looks a little weird because 'Bar' is an inner class)

      String result = "<h:panelGroup>";
      result += "<ui:param name=\"_collection\" value=\"#{foo.bar}\"/>";
      result += "<h:dataTable id=\"fooBar\" value=\"#{forgeview:asList(_collection)}\" var=\"_item\">";
      result += "<h:column><f:facet name=\"header\"><h:outputText value=\"Name\"/></f:facet>";
      result += "<h:link outcome=\"/scaffold/forgeWidgetBuilderTest$Bar/view\" value=\"#{_item.name}\"><f:param name=\"id\" value=\"#{_item.id}\"/></h:link>";
      result += "</h:column>";
      result += "<h:column><f:facet name=\"header\"><h:outputText value=\"Description\"/></f:facet>";
      result += "<h:link outcome=\"/scaffold/forgeWidgetBuilderTest$Bar/view\" value=\"#{_item.description}\"><f:param name=\"id\" value=\"#{_item.id}\"/></h:link>";
      result += "</h:column>";
      result += "<h:column><h:commandLink action=\"#{_collection.remove(_item)}\" value=\"Remove\"/></h:column>";
      result += "</h:dataTable>";
      result += "<h:selectOneMenu converter=\"#{forgeWidgetBuilderTest$BarBean.converter}\" value=\"#{requestScope['fooBarAdd']}\"><f:selectItem/><f:selectItems value=\"#{forgeWidgetBuilderTest$BarBean.all}\"/></h:selectOneMenu>";
      result += "<h:commandLink action=\"#{_collection.add(requestScope['fooBarAdd'])}\" value=\"Add\"/>";
      result += "</h:panelGroup>";

      assertEquals(result, widget.toString());
   }

   //
   // Inner class
   //

   static class Bar
   {
      public String name;

      @UiComesAfter("name")
      public String description;
   }
}
