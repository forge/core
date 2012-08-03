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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.metawidget.inspector.annotation.MetawidgetAnnotationInspector;
import org.metawidget.inspector.annotation.UiComesAfter;
import org.metawidget.inspector.annotation.UiRequired;
import org.metawidget.inspector.composite.CompositeInspector;
import org.metawidget.inspector.composite.CompositeInspectorConfig;
import org.metawidget.inspector.impl.BaseObjectInspectorConfig;
import org.metawidget.inspector.impl.propertystyle.PropertyStyle;
import org.metawidget.inspector.impl.propertystyle.javabean.JavaBeanPropertyStyleConfig;
import org.metawidget.inspector.impl.propertystyle.statically.StaticPropertyStyle;
import org.metawidget.inspector.propertytype.PropertyTypeInspector;
import org.metawidget.statically.StaticWidget;
import org.metawidget.statically.StaticXmlWidget;
import org.metawidget.statically.faces.component.html.StaticHtmlMetawidget;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlWidgetBuilder;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlWidgetBuilderConfig;
import org.metawidget.statically.layout.SimpleLayout;
import org.metawidget.util.CollectionUtils;

import junit.framework.TestCase;

public class HtmlSearchWidgetBuilderTest extends TestCase
{

   public void testLookup() throws Exception
   {
      // Without 'required'

      HtmlSearchWidgetBuilder widgetBuilder = new HtmlSearchWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(LOOKUP, "Foo, Bar, Baz");
      StaticWidget widget = widgetBuilder.buildWidget(PROPERTY, attributes, null);
      assertEquals(
               "<h:selectOneMenu><f:selectItem/><f:selectItem itemValue=\"Foo\"/><f:selectItem itemValue=\"Bar\"/><f:selectItem itemValue=\"Baz\"/></h:selectOneMenu>",
               widget.toString());

      // With 'required'

      attributes.put(REQUIRED, TRUE);
      widget = widgetBuilder.buildWidget(PROPERTY, attributes, null);
      assertEquals(
               "<h:selectOneMenu><f:selectItem/><f:selectItem itemValue=\"Foo\"/><f:selectItem itemValue=\"Bar\"/><f:selectItem itemValue=\"Baz\"/></h:selectOneMenu>",
               widget.toString());
   }

   public void testFacesLookup() throws Exception
   {
      // Without 'required'

      HtmlSearchWidgetBuilder widgetBuilder = new HtmlSearchWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(FACES_LOOKUP, "#{foo.bar}");
      StaticWidget widget = widgetBuilder.buildWidget(PROPERTY, attributes, null);
      assertEquals("<h:selectOneMenu><f:selectItem/><f:selectItems value=\"#{foo.bar}\"/></h:selectOneMenu>",
               widget.toString());

      // With 'required'

      attributes.put(REQUIRED, TRUE);
      widget = widgetBuilder.buildWidget(PROPERTY, attributes, null);
      assertEquals("<h:selectOneMenu><f:selectItem/><f:selectItems value=\"#{foo.bar}\"/></h:selectOneMenu>",
               widget.toString());
   }

   public void testCollection()
            throws Exception
   {

      // Unsupported type

      StaticHtmlMetawidget metawidget = new StaticHtmlMetawidget();
      HtmlWidgetBuilder widgetBuilder = new HtmlWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(TYPE, Set.class.getName());
      StaticWidget widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);
      assertEquals("<stub/>", widget.toString());

      // Most basic

      attributes.put(TYPE, List.class.getName());
      widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);
      assertEquals(
               "<h:dataTable var=\"_item\"><h:column><f:facet name=\"header\"><h:outputText value=\"\"/></f:facet><h:outputText value=\"#{_item}\"/></h:column></h:dataTable>",
               widget.toString());

      // With parent name

      attributes.put(NAME, "items");
      widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);
      assertEquals(
               "<h:dataTable var=\"_item\"><h:column><f:facet name=\"header\"><h:outputText value=\"Items\"/></f:facet><h:outputText value=\"#{_item}\"/></h:column></h:dataTable>",
               widget.toString());

      // With Array

      attributes.put(TYPE, Foo[].class.getName());
      widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);
      assertEquals(
               "<h:dataTable var=\"_item\"><h:column><f:facet name=\"header\"><h:outputText value=\"Bar\"/></f:facet><h:outputText id=\"itemBar\" value=\"#{_item.bar}\"/></h:column><h:column><f:facet name=\"header\"><h:outputText value=\"Baz\"/></f:facet><h:outputText id=\"itemBaz\" value=\"#{_item.baz}\"/></h:column></h:dataTable>",
               widget.toString());

      // With PARAMETERIZED_TYPE

      attributes.put(TYPE, List.class.getName());
      attributes.put(PARAMETERIZED_TYPE, Foo.class.getName());
      widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);
      assertEquals(
               "<h:dataTable var=\"_item\"><h:column><f:facet name=\"header\"><h:outputText value=\"Bar\"/></f:facet><h:outputText id=\"itemBar\" value=\"#{_item.bar}\"/></h:column><h:column><f:facet name=\"header\"><h:outputText value=\"Baz\"/></f:facet><h:outputText id=\"itemBaz\" value=\"#{_item.baz}\"/></h:column></h:dataTable>",
               widget.toString());

      // With non-recursable PARAMETERIZED_TYPE

      attributes.put(TYPE, List.class.getName());
      attributes.put(PARAMETERIZED_TYPE, String.class.getName());
      widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);
      assertEquals(
               "<h:dataTable var=\"_item\"><h:column><f:facet name=\"header\"><h:outputText value=\"Items\"/></f:facet><h:outputText value=\"#{_item}\"/></h:column></h:dataTable>",
               widget.toString());

      // From Metawidget

      PropertyStyle propertyStyle = new StaticPropertyStyle();
      metawidget.setInspector(new CompositeInspector(new CompositeInspectorConfig().setInspectors(
               new PropertyTypeInspector(new BaseObjectInspectorConfig().setPropertyStyle(propertyStyle)),
               new MetawidgetAnnotationInspector(new BaseObjectInspectorConfig().setPropertyStyle(propertyStyle)))));
      metawidget.setValue("#{foo.pageItems}");
      metawidget.setPath(FooBean.class.getName() + "/pageItems");
      metawidget.setLayout(new SimpleLayout());

      String result = "<h:dataTable id=\"fooPageItems\" value=\"#{foo.pageItems}\" var=\"_item\">" +
               "<h:column>" +
               "<f:facet name=\"header\">" +
               "<h:outputText value=\"Bar\"/>" +
               "</f:facet>" +
               "<h:outputText id=\"itemBar\" value=\"#{_item.bar}\"/>" +
               "</h:column>" +
               "<h:column>" +
               "<f:facet name=\"header\">" +
               "<h:outputText value=\"Baz\"/>" +
               "</f:facet>" +
               "<h:outputText id=\"itemBaz\" value=\"#{_item.baz}\"/>" +
               "</h:column>" +
               "</h:dataTable>";

      assertEquals(result, metawidget.toString());

      // With required columns

      metawidget.setValue(null);
      metawidget.setPath(FooBean.class.getName() + "/requiredPageItems");
      metawidget.setLayout(new SimpleLayout());

      result = "<h:dataTable var=\"_item\">" +
               "<h:column>" +
               "<f:facet name=\"header\">" +
               "<h:outputText value=\"Bar\"/>" +
               "</f:facet>" +
               "<h:outputText id=\"itemBar\" value=\"#{_item.bar}\"/>" +
               "</h:column>" +
               "<h:column>" +
               "<f:facet name=\"header\">" +
               "<h:outputText value=\"Abc\"/>" +
               "</f:facet>" +
               "<h:outputText id=\"itemAbc\" value=\"#{_item.abc}\"/>" +
               "</h:column>" +
               "</h:dataTable>";

      assertEquals(result, metawidget.toString());
   }

   public void testCollectionWithManyColumns()
            throws Exception
   {

      StaticHtmlMetawidget metawidget = new StaticHtmlMetawidget();
      metawidget.setInspector(new PropertyTypeInspector(
               new BaseObjectInspectorConfig().setPropertyStyle(new StaticPropertyStyle(
                        new JavaBeanPropertyStyleConfig().setSupportPublicFields(true)))));
      HtmlWidgetBuilder widgetBuilder = new HtmlWidgetBuilder();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(TYPE, List.class.getName());
      attributes.put(PARAMETERIZED_TYPE, LargeFoo.class.getName());
      StaticXmlWidget widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);

      String result = "<h:dataTable var=\"_item\">" +
               "<h:column>" +
               "<f:facet name=\"header\">" +
               "<h:outputText value=\"Column 1\"/>" +
               "</f:facet>" +
               "<h:outputText id=\"itemColumn1\" value=\"#{_item.column1}\"/>" +
               "</h:column>" +
               "<h:column>" +
               "<f:facet name=\"header\">" +
               "<h:outputText value=\"Column 2\"/>" +
               "</f:facet>" +
               "<h:outputText id=\"itemColumn2\" value=\"#{_item.column2}\"/>" +
               "</h:column>" +
               "<h:column>" +
               "<f:facet name=\"header\">" +
               "<h:outputText value=\"Column 3\"/>" +
               "</f:facet>" +
               "<h:outputText id=\"itemColumn3\" value=\"#{_item.column3}\"/>" +
               "</h:column>" +
               "<h:column>" +
               "<f:facet name=\"header\">" +
               "<h:outputText value=\"Column 4\"/>" +
               "</f:facet>" +
               "<h:outputText id=\"itemColumn4\" value=\"#{_item.column4}\"/>" +
               "</h:column>" +
               "<h:column>" +
               "<f:facet name=\"header\">" +
               "<h:outputText value=\"Column 5\"/>" +
               "</f:facet>" +
               "<h:outputText id=\"itemColumn5\" value=\"#{_item.column5}\"/>" +
               "</h:column>" +
               // Column 6 should be suppressed
               "</h:dataTable>";

      assertEquals(result, widget.toString());

      widgetBuilder = new HtmlWidgetBuilder(new HtmlWidgetBuilderConfig().setMaximumColumnsInDataTable(2));
      widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);

      result = "<h:dataTable var=\"_item\">" +
               "<h:column>" +
               "<f:facet name=\"header\">" +
               "<h:outputText value=\"Column 1\"/>" +
               "</f:facet>" +
               "<h:outputText id=\"itemColumn1\" value=\"#{_item.column1}\"/>" +
               "</h:column>" +
               "<h:column>" +
               "<f:facet name=\"header\">" +
               "<h:outputText value=\"Column 2\"/>" +
               "</f:facet>" +
               "<h:outputText id=\"itemColumn2\" value=\"#{_item.column2}\"/>" +
               "</h:column>" +
               // Column 3+ should be suppressed
               "</h:dataTable>";

      assertEquals(result, widget.toString());

      widgetBuilder = new HtmlWidgetBuilder(new HtmlWidgetBuilderConfig().setMaximumColumnsInDataTable(0));
      widget = widgetBuilder.buildWidget(PROPERTY, attributes, metawidget);

      result = "<h:dataTable var=\"_item\">" +
               "<h:column>" +
               "<f:facet name=\"header\">" +
               "<h:outputText value=\"Column 1\"/>" +
               "</f:facet>" +
               "<h:outputText id=\"itemColumn1\" value=\"#{_item.column1}\"/>" +
               "</h:column>" +
               "<h:column>" +
               "<f:facet name=\"header\">" +
               "<h:outputText value=\"Column 2\"/>" +
               "</f:facet>" +
               "<h:outputText id=\"itemColumn2\" value=\"#{_item.column2}\"/>" +
               "</h:column>" +
               "<h:column>" +
               "<f:facet name=\"header\">" +
               "<h:outputText value=\"Column 3\"/>" +
               "</f:facet>" +
               "<h:outputText id=\"itemColumn3\" value=\"#{_item.column3}\"/>" +
               "</h:column>" +
               "<h:column>" +
               "<f:facet name=\"header\">" +
               "<h:outputText value=\"Column 4\"/>" +
               "</f:facet>" +
               "<h:outputText id=\"itemColumn4\" value=\"#{_item.column4}\"/>" +
               "</h:column>" +
               "<h:column>" +
               "<f:facet name=\"header\">" +
               "<h:outputText value=\"Column 5\"/>" +
               "</f:facet>" +
               "<h:outputText id=\"itemColumn5\" value=\"#{_item.column5}\"/>" +
               "</h:column>" +
               "<h:column>" +
               "<f:facet name=\"header\">" +
               "<h:outputText value=\"Column 6\"/>" +
               "</f:facet>" +
               "<h:outputText id=\"itemColumn6\" value=\"#{_item.column6}\"/>" +
               "</h:column>" +
               "</h:dataTable>";

      assertEquals(result, widget.toString());
   }

   //
   // Inner class
   //

   static class FooBean
   {

      public List<Foo> getPageItems()
      {

         return null;
      }

      public List<RequiredFoo> getRequiredPageItems()
      {

         return null;
      }
   }

   static class Foo
   {

      public String getBar()
      {

         return null;
      }

      public void setBar(String bar)
      {

         // Do nothing
      }

      public String getBaz()
      {

         return null;
      }

      public void setBaz(String baz)
      {

         // Do nothing
      }
   }

   static class RequiredFoo
   {

      @UiRequired
      public String getBar()
      {

         return null;
      }

      public void setBar(String bar)
      {

         // Do nothing
      }

      public String getBaz()
      {

         return null;
      }

      public void setBaz(String baz)
      {

         // Do nothing
      }

      @UiRequired
      @UiComesAfter("baz")
      public String getAbc()
      {

         return null;
      }

      public void setAbc(String abc)
      {

         // Do nothing
      }
   }

   static class LargeFoo
   {

      // Uppercase must be decapitalized within EL

      public String Column1;

      public String column2;

      public String column3;

      public String column4;

      public String column5;

      public String column6;
   }
}
